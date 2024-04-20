package dk.gtz.graphedit.spi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgress;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgressType;
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.model.lsp.ModelNotificationLevel;
import dk.gtz.graphedit.proto.Buffer;
import dk.gtz.graphedit.proto.Capability;
import dk.gtz.graphedit.proto.DiagnosticsList;
import dk.gtz.graphedit.proto.Diff;
import dk.gtz.graphedit.proto.Edge;
import dk.gtz.graphedit.proto.Empty;
import dk.gtz.graphedit.proto.Graph;
import dk.gtz.graphedit.proto.LanguageServerGrpc;
import dk.gtz.graphedit.proto.LanguageServerGrpc.LanguageServerStub;
import dk.gtz.graphedit.proto.Notification;
import dk.gtz.graphedit.proto.NotificationLevel;
import dk.gtz.graphedit.proto.Polygon;
import dk.gtz.graphedit.proto.ProgressReport;
import dk.gtz.graphedit.proto.ProgressReportType;
import dk.gtz.graphedit.proto.Project;
import dk.gtz.graphedit.proto.ServerInfo;
import dk.gtz.graphedit.proto.Severity;
import dk.gtz.graphedit.proto.Vertex;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.util.MetadataUtils;
import dk.gtz.graphedit.util.PlatformUtils;
import dk.gtz.graphedit.util.RetryUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelDiff;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import dk.yalibs.yafunc.IRunnable1;
import dk.yalibs.yalazy.Lazy;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;

/**
 * Language server base class for wiring a gRPC based language server with graphedit.
 *
 * Extend this clas and override the appropriate subprocess invocation parts to seamlessly integrate your MLSP implementation.
 */
public abstract class GrpcLanguageServer implements ILanguageServer {
	private final Logger logger = LoggerFactory.getLogger(GrpcLanguageServer.class);

	/**
	 * Lazy loaded gRPC language server stub interface instance.
	 * Use this to interact with the connected language server.
	 */
	protected final Lazy<LanguageServerStub> stub;

	/**
	 * Lazy loaded server info struct.
	 */
	protected final Lazy<ServerInfo> serverInfo;

	/**
	 * Utility instance of the gRPC Empty value.
	 */
	protected final Empty empty;

	/**
	 * Reference to the current editor buffercontainer
	 */
	protected IBufferContainer bufferContainer;

	/**
	 * Host string, pointing to the connected language server
	 */
	protected final String host;

	/**
	 * Host port, pointing to the connected language server
	 */
	protected final int port;

	/**
	 * The thread in which the language server process is running
	 */
	protected final Thread programThread;

	/**
	 * Maximum amount of attempts to connect to the language server
	 */
	protected int maxConnectionAttempts = 60;

	/**
	 * Time (in milliseconds) to wait between failed connection attempts
	 */
	protected int connectionAttemptWaitMilliseconds = 1000;
	private final Converter converter;

	/**
	 * Constructs a new GrpcLanguageServer instance
	 * @param command The subprocess command to execute at startup
	 * @param arguments The arguments to provide to the subprocess command
	 */
	protected GrpcLanguageServer(String command, List<String> arguments) {
		this("0.0.0.0", new Random().nextInt(5000, 6000), command, arguments);
	}

	/**
	 * Constructs a new GrpcLanguageServer instance
	 * @param host The gRPC host to connect to
	 * @param port The gRPC port to connect to
	 * @param command The subprocess command to execute at startup
	 * @param arguments The arguments to provide to the subprocess command
	 */
	protected GrpcLanguageServer(String host, int port, String command, List<String> arguments) {
		this.host = host;
		this.port = port;
		this.converter = new Converter();
		this.programThread = new Thread(() -> PlatformUtils.launchProgram(command, arguments));
		this.programThread.start();
		this.stub = new Lazy<>(() -> RetryUtils.tryTimes(maxConnectionAttempts, connectionAttemptWaitMilliseconds, this::connect));
		this.empty = Empty.newBuilder().build();
		this.serverInfo = new Lazy<>(this::getServerInfo);
	}

	/**
	 * Connect to the language server.
	 * @return A new language server stub instance
	 */
	protected LanguageServerStub connect() {
		if(!programThread.isAlive())
			programThread.start();
		var channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		return LanguageServerGrpc.newStub(channel);
	}

	/**
	 * Get the information about the language server.
	 * @return A class of language server information
	 */
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

	/**
	 * Check if the language server reports that it is capable of some feature.
	 * @param capability The feature capability
	 * @return {@code true} if the server reports that it is capable of the provided feature, otherwise {@code false}
	 */
	protected boolean isServerCapable(Capability capability) {
		return serverInfo.get().getCapabilitiesList().contains(capability);
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

	/**
	 * Tell the connected language server to handle the provided diff
	 * @param diff The diff for the connected server to handle
	 */
	protected void handleDiff(Diff diff) {
		if(!isServerCapable(Capability.CAPABILITY_DIFFS))
			return;
		var so = new SingleResponseStreamObserver<Empty>();
		stub.get().handleDiff(diff, so);
	}

	private void bufferChanged(ISyntaxFactory factory, String bufferName, ObjectProperty<ModelProjectResource> oldVal, ViewModelProjectResource newVal) {
		var syntaxName = newVal.getSyntaxName();
		if(syntaxName.isEmpty())
			return;
		if(!syntaxName.get().equals(getLanguageName()))
			return;
		var a = new ViewModelProjectResource(oldVal.get(), factory);
		if(!ViewModelDiff.areComparable(a, newVal))
			return;
		var diff = ViewModelDiff.compare(a, newVal);
		oldVal.set(newVal.toModel());
		var gDiff = converter.toDiff(diff, bufferName);
		handleDiff(gDiff);
	}

	private void projectOpened() {
		if(!isServerCapable(Capability.CAPABILITY_PROJECT))
			return;
		var project = DI.get(ViewModelProject.class);
		var builder = Project.newBuilder()
			.setPath(project.rootDirectory().get())
			.setName(project.name().get());
		for(var excludeFile : project.excludeFiles())
			builder.addExcludeFiles(excludeFile.get());
		for(var metadata : project.metadata())
			builder.putMetadata(metadata.getKey().get(), metadata.getValue().getValue());
		var so = new SingleResponseStreamObserver<Empty>();
		stub.get().projectOpened(builder.build(), so);
	}

	@Override
	public void initialize(File projectFile, IBufferContainer bufferContainer) {
		projectOpened();
		final var syntaxFactory = MetadataUtils.getSyntaxFactory(getLanguageName());
		this.bufferContainer = bufferContainer;
		this.bufferContainer.getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
			if(c.wasAdded()) {
				var so = new SingleResponseStreamObserver<Empty>();
				var changedVal = c.getValueAdded();
				stub.get().bufferCreated(converter.toBuffer(changedVal, c.getKey()), so);
				if(!isServerCapable(Capability.CAPABILITY_DIFFS))
					return;
				var old = new SimpleObjectProperty<>(changedVal.toModel());
				changedVal.addListener((e,o,n) -> bufferChanged(syntaxFactory, c.getKey(), old, n));
			}
			if(c.wasRemoved()) {
				var so = new SingleResponseStreamObserver<Empty>();
				stub.get().bufferDeleted(converter.toBuffer(c.getValueRemoved(), c.getKey()), so);
			}
		});
	}

	@Override
	public Collection<ModelLint> getDiagnostics() {
		logger.debug("diagnostics for this language server are gRPC stream only");
		return List.of();
	}

	@Override
	public void addDiagnosticsCallback(IRunnable1<Collection<ModelLint>> callback) {
		if(!isServerCapable(Capability.CAPABILITY_DIAGNOSTICS))
			return;
		stub.get().getDiagnostics(empty, new StreamObserver<>() {
			@Override
			public void onNext(DiagnosticsList value) {
				var converted = new ArrayList<ModelLint>();
				for(var protoDiagnostic : value.getDiagnosticsList())
					converted.add(new ModelLint(
								protoDiagnostic.getModelkey(),
								protoDiagnostic.getLintIdentifier(),
								converter.toSeverity(protoDiagnostic.getSeverity()),
								protoDiagnostic.getTitle(),
								protoDiagnostic.getMessage(),
								Optional.of(protoDiagnostic.getDescription()),
								converter.toUUIDList(protoDiagnostic.getAffectedElementsList()),
								converter.toPolygonList(protoDiagnostic.getAffectedRegionsList())));
				callback.run(converted);
			}

			@Override
			public void onError(Throwable t) {
				logger.error("{}", t.getMessage());
			}

			@Override
			public void onCompleted() {
				logger.info("diagnostics completed");
			}
		});
	}

	@Override
	public void addNotificationCallback(IRunnable1<ModelNotification> callback) {
		if(!isServerCapable(Capability.CAPABILITY_NOTIFICATIONS))
			return;
		stub.get().getNotifications(empty, new StreamObserver<>() {
			@Override
			public void onNext(Notification value) {
				callback.run(new ModelNotification(converter.toNotificationLevel(value.getLevel()), value.getMessage()));
			}

			@Override
			public void onError(Throwable t) {
				logger.error("{}", t.getMessage());
			}

			@Override
			public void onCompleted() {
				logger.info("notifications completed");
			}
		});
	}

	@Override
	public void addProgressCallback(IRunnable1<ModelLanguageServerProgress> callback) {
		if(!isServerCapable(Capability.CAPABILITY_PROGRESS))
			return;
		stub.get().getProgress(empty, new StreamObserver<>() {
			@Override
			public void onNext(ProgressReport value) {
				callback.run(new ModelLanguageServerProgress(
							value.getToken(),
							converter.toProgressType(value.getType()),
							value.getTitle(),
							value.getMessage()));
			}

			@Override
			public void onError(Throwable t) {
				logger.error("{}", t.getMessage());
			}

			@Override
			public void onCompleted() {
				logger.info("notifications completed");
			}
		});
	}

	private static class Converter {
		public List<UUID> toUUIDList(List<String> ids) {
			return ids.stream().map(UUID::fromString).toList();
		}

		public List<List<ModelPoint>> toPolygonList(List<Polygon> polys) {
			return polys.stream().map(this::toPolygon).toList();
		}

		public List<ModelPoint> toPolygon(Polygon poly) {
			return poly.getPointsList().stream().map(p -> new ModelPoint(p.getX(), p.getY())).toList();
		}

		public ModelLintSeverity toSeverity(Severity severity) {
			return switch(severity) {
				case SEVERITY_ERROR -> ModelLintSeverity.ERROR;
				case SEVERITY_HINT -> ModelLintSeverity.HINT;
				case SEVERITY_INFO -> ModelLintSeverity.INFO;
				case SEVERITY_WARNING -> ModelLintSeverity.WARNING;
				default -> ModelLintSeverity.ERROR; // NOTE: deliberately annoying
			};
		}

		public ModelLanguageServerProgressType toProgressType(ProgressReportType type) {
			return switch (type) {
				case PROGRESS_BEGIN -> ModelLanguageServerProgressType.BEGIN;
				case PROGRESS_END -> ModelLanguageServerProgressType.END;
				case PROGRESS_END_FAIL -> ModelLanguageServerProgressType.END_FAIL;
				case PROGRESS_STATUS -> ModelLanguageServerProgressType.PROGRESS;
				case UNRECOGNIZED -> ModelLanguageServerProgressType.END_FAIL;
				default -> ModelLanguageServerProgressType.END_FAIL;
			};
		}

		public ModelNotificationLevel toNotificationLevel(NotificationLevel level) {
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

		public Vertex toVertex(ViewModelVertex vertex) {
			var serializer = DI.get(IModelSerializer.class);
			return Vertex.newBuilder()
				.setId(vertex.id().toString())
				.setJsonEncoding(serializer.serialize(vertex.toModel()))
				.build();
		}

		public Edge toEdge(ViewModelEdge edge) {
			var serializer = DI.get(IModelSerializer.class);
			return Edge.newBuilder()
				.setId(edge.id().toString())
				.setJsonEncoding(serializer.serialize(edge.toModel()))
				.build();
		}

		public Diff toDiff(ViewModelDiff diff, String bufferName) {
			var b = Diff.newBuilder()
				.setBufferName(bufferName)
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

		public Buffer toBuffer(ViewModelProjectResource buffer, String key) {
			var b = Buffer.newBuilder();
			b.setPath(key);
			b.putAllMetadata(buffer.metadata());
			b.setGraph(toGraph(buffer.syntax()));
			return b.build();
		}

		public Graph toGraph(ViewModelGraph graph) {
			var b = Graph.newBuilder();
			for(var v : graph.vertices().entrySet())
				b.addVertices(toVertex(v.getValue()));
			for(var e : graph.edges().entrySet())
				b.addEdges(toEdge(e.getValue()));
			return b.build();
		}
	}
}
