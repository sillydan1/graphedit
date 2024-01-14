package dk.gtz.graphedit.spi;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgress;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgressType;
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.model.lsp.ModelNotificationLevel;
import dk.gtz.graphedit.proto.DiagnosticsList;
import dk.gtz.graphedit.proto.Diff;
import dk.gtz.graphedit.proto.Edge;
import dk.gtz.graphedit.proto.Empty;
import dk.gtz.graphedit.proto.LanguageServerGrpc;
import dk.gtz.graphedit.proto.LanguageServerGrpc.LanguageServerStub;
import dk.gtz.graphedit.proto.Notification;
import dk.gtz.graphedit.proto.NotificationLevel;
import dk.gtz.graphedit.proto.Polygon;
import dk.gtz.graphedit.proto.ProgressReport;
import dk.gtz.graphedit.proto.ProgressReportType;
import dk.gtz.graphedit.proto.ServerInfo;
import dk.gtz.graphedit.proto.Severity;
import dk.gtz.graphedit.proto.Vertex;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.util.MetadataUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelDiff;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import dk.yalibs.yafunc.IRunnable1;
import dk.yalibs.yalazy.Lazy;
import dk.yalibs.yastreamgobbler.StreamGobbler;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;

public abstract class GrpcLanguageServer implements ILanguageServer {
	private final Logger logger = LoggerFactory.getLogger(GrpcLanguageServer.class);
	protected final Lazy<LanguageServerStub> stub;
	protected final Lazy<ServerInfo> serverInfo;
	protected final Empty empty;
	protected IBufferContainer bufferContainer;
	protected final String host;
	protected final int port;
	protected Thread programThread;

	public GrpcLanguageServer(String command, List<String> arguments) {
		this("0.0.0.0", new Random().nextInt(5000, 6000), command, arguments);
	}

	public GrpcLanguageServer(String host, int port, String command, List<String> arguments) {
		this.host = host;
		this.port = port;
		this.programThread = new Thread(() -> launchProgram(command, arguments));
		this.stub = new Lazy<>(() -> tryTimes(10, 1000, () -> connect(250)));
		this.empty = Empty.newBuilder().build();
		this.serverInfo = new Lazy<>(this::getServerInfo);
	}

	protected void launchProgram(String command, List<String> arguments) {
		Process p = null;
		try {
			var pb = new ProcessBuilder();
			pb.command(command);
			pb.directory(Path.of(command).getParent().toFile());
			for(var argument : arguments)
				pb.command().add(argument);
			pb.redirectErrorStream(true);
			p = pb.start();
			new Thread(new StreamGobbler(p.getInputStream(), logger::trace)).start();
			new Thread(new StreamGobbler(p.getErrorStream(), logger::error)).start();
			Runtime.getRuntime().addShutdownHook(new Thread(p::destroy));
			p.waitFor();
			var exitCode = p.exitValue();
			if(exitCode != 0)
				logger.warn("language server process exited with code: {}", exitCode);
		} catch(InterruptedException e) {
			logger.warn("<{}> language server process was interrupted", serverInfo.isPresent() ? serverInfo.get().getName() : "?");
			if(p != null)
				p.destroy();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			if(p != null)
				p.destroy();
		}
	}

	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch(InterruptedException e2) {
			// ignored
		}
	}

	private <T> T tryTimes(int maxAttempts, int sleepMillis, Supplier<T> f) {
		for(var attempts = 0; attempts < maxAttempts; attempts++) {
			try {
				return f.get();
			} catch(Exception e) {
				logger.warn("'{}' {}/{} attempts left", e.getMessage(), attempts, maxAttempts);
				sleep(sleepMillis);
			}
		}
		throw new RuntimeException("too many attempts");
	}

	protected LanguageServerStub connect(int threadWaitMillis) {
		if(!programThread.isAlive())
			programThread.start();
		if(threadWaitMillis > 0)
			sleep(threadWaitMillis);
		var channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		return LanguageServerGrpc.newStub(channel);
	}

	protected ServerInfo getServerInfo() {
		try {
			var so = new SingleResponseStreamObserver<ServerInfo>();
			stub.get().getServerInfo(empty, so);
			so.await();
			return so.get();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getLanguageName() {
		return serverInfo.get().getLanguage();
	}

	@Override
	public String getServerName() {
		return serverInfo.get().getName();
	}

	@Override
	public String getServerVersion() {
		return serverInfo.get().getSemanticVersion();
	}

	protected void handleDiff(Diff diff) {
		try {
			var so = new SingleResponseStreamObserver<Empty>();
			stub.get().handleDiff(diff, so);
			so.await(); // TODO: Consider if this should be asynchronous
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(File projectFile, IBufferContainer bufferContainer) {
		final var ltsSyntax = MetadataUtils.getSyntaxFactory(getLanguageName());
		this.bufferContainer = bufferContainer;
		this.bufferContainer.getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
			if(c.wasAdded()) {
				var changedVal = c.getValueAdded();
				var old = new SimpleObjectProperty<>(changedVal.toModel());
				changedVal.addListener((e,o,n) -> {
					var syntaxName = n.getSyntaxName();
					if(syntaxName.isEmpty())
						return;
					if(!syntaxName.get().equals(getLanguageName()))
						return;
					var a = new ViewModelProjectResource(old.get(), ltsSyntax);
					if(!ViewModelDiff.areComparable(a, n))
						return;
					var diff = ViewModelDiff.compare(a, n);
					old.set(n.toModel());
					// TODO: only send if there are semantically significant changes(?) - could also be an extra function that you override
					var gDiff = toDiff(diff);
					handleDiff(gDiff);
				});
			}
		});
	}

	@Override
	public Collection<ModelLint> getDiagnostics() {
		logger.trace("diagnostics for this language server are gRPC stream only. Returning [] instead");
		return List.of();
	}

	private List<UUID> toUUIDList(List<String> ids) {
		return ids.stream().map(UUID::fromString).toList();
	}

	private List<List<ModelPoint>> toPolygonList(List<Polygon> polys) {
		return polys.stream().map(this::toPolygon).toList();
	}

	private List<ModelPoint> toPolygon(Polygon poly) {
		return poly.getPointsList().stream().map(p -> new ModelPoint(p.getX(), p.getY())).toList();
	}

	private ModelLintSeverity toSeverity(Severity severity) {
		return switch(severity) {
			case SEVERITY_ERROR -> ModelLintSeverity.ERROR;
			case SEVERITY_HINT -> ModelLintSeverity.HINT;
			case SEVERITY_INFO -> ModelLintSeverity.INFO;
			case SEVERITY_WARNING -> ModelLintSeverity.WARNING;
			default -> ModelLintSeverity.ERROR; // NOTE: deliberately annoying
		};
	}

	private ModelLanguageServerProgressType toProgressType(ProgressReportType type) {
		return switch (type) {
			case PROGRESS_BEGIN -> ModelLanguageServerProgressType.BEGIN;
			case PROGRESS_END -> ModelLanguageServerProgressType.END;
			case PROGRESS_END_FAIL -> ModelLanguageServerProgressType.END_FAIL;
			case PROGRESS_STATUS -> ModelLanguageServerProgressType.PROGRESS;
			case UNRECOGNIZED -> ModelLanguageServerProgressType.END_FAIL;
			default -> ModelLanguageServerProgressType.END_FAIL;
		};
	}

	private ModelNotificationLevel toNotificationLevel(NotificationLevel level) {
		return switch (level) {
			case NOTIFICATION_ERROR -> ModelNotificationLevel.ERROR;
			case NOTIFICATION_WARNING -> ModelNotificationLevel.WARNING;
			case NOTIFICATION_DEBUG -> ModelNotificationLevel.DEBUG;
			case NOTIFICATION_INFO -> ModelNotificationLevel.INFO;
			case NOTIFICATION_TRACE -> ModelNotificationLevel.TRACE;
			case UNRECOGNIZED -> ModelNotificationLevel.TRACE;
			default -> ModelNotificationLevel.TRACE;
		};
	}

	private Vertex toVertex(ViewModelVertex vertex) {
		var serializer = DI.get(IModelSerializer.class);
		return Vertex.newBuilder()
			.setJsonEncoding(serializer.serialize(vertex.toModel()))
			.build();
	}

	private Edge toEdge(ViewModelEdge edge) {
		var serializer = DI.get(IModelSerializer.class);
		return Edge.newBuilder()
			.setJsonEncoding(serializer.serialize(edge.toModel()))
			.build();
	}

	private Diff toDiff(ViewModelDiff diff) {
		var b = Diff.newBuilder()
			.setSyntaxStyle(diff.getSyntaxStyle());
		for(var v : diff.getVertexAdditions())
			b.addVertexAdditions(toVertex(v));
		for(var v : diff.getVertexDeletions())
			b.addVertexDeletions(toVertex(v));
		for(var e : diff.getEdgeAdditions())
			b.addEdgeAdditions(toEdge(e));
		for(var e : diff.getEdgeDeletions())
			b.addEdgeDeletions(toEdge(e));
		return b.build();
	}

	@Override
	public void addDiagnosticsCallback(IRunnable1<Collection<ModelLint>> callback) {
		stub.get().getDiagnostics(empty, new StreamObserver<>() {
			@Override
			public void onNext(DiagnosticsList value) {
				var converted = new ArrayList<ModelLint>();
				for(var protoDiagnostic : value.getDiagnosticsList())
					converted.add(new ModelLint(
								protoDiagnostic.getModelkey(),
								protoDiagnostic.getLintIdentifier(),
								toSeverity(protoDiagnostic.getSeverity()),
								protoDiagnostic.getTitle(),
								protoDiagnostic.getMessage(),
								Optional.of(protoDiagnostic.getDescription()),
								toUUIDList(protoDiagnostic.getAffectedElementsList()),
								toPolygonList(protoDiagnostic.getAffectedRegionsList())));
				callback.run(converted);
			}

			@Override
			public void onError(Throwable t) {
				logger.error("{}", t.getMessage()); // TODO: Consider doing a server progress call
			}

			@Override
			public void onCompleted() {
				logger.info("diagnostics completed");
			}
		});
	}

	@Override
	public void addNotificationCallback(IRunnable1<ModelNotification> callback) {
		stub.get().getNotifications(empty, new StreamObserver<>() {
			@Override
			public void onNext(Notification value) {
				callback.run(new ModelNotification(toNotificationLevel(value.getLevel()), value.getMessage()));
			}

			@Override
			public void onError(Throwable t) {
				logger.error("{}", t.getMessage()); // TODO: Consider doing a server progress call
			}

			@Override
			public void onCompleted() {
				logger.info("notifications completed");
			}
		});
	}

	@Override
	public void addProgressCallback(IRunnable1<ModelLanguageServerProgress> callback) {
		stub.get().getProgress(empty, new StreamObserver<>() {
			@Override
			public void onNext(ProgressReport value) {
				callback.run(new ModelLanguageServerProgress(
							value.getToken(),
							toProgressType(value.getType()),
							value.getTitle(),
							value.getMessage()));
			}

			@Override
			public void onError(Throwable t) {
				logger.error("{}", t.getMessage()); // TODO: Consider doing a server progress call
			}

			@Override
			public void onCompleted() {
				logger.info("notifications completed");
			}
		});
	}
}
