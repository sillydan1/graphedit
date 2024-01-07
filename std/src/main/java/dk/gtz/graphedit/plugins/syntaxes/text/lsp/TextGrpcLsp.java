package dk.gtz.graphedit.plugins.syntaxes.text.lsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.DiagnosticsList;
import dk.gtz.graphedit.Empty;
import dk.gtz.graphedit.LanguageServerGrpc;
import dk.gtz.graphedit.LanguageServerGrpc.LanguageServerStub;
import dk.gtz.graphedit.Notification;
import dk.gtz.graphedit.NotificationLevel;
import dk.gtz.graphedit.Polygon;
import dk.gtz.graphedit.ProgressReport;
import dk.gtz.graphedit.ProgressReportType;
import dk.gtz.graphedit.ServerInfo;
import dk.gtz.graphedit.Severity;
import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgress;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgressType;
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.model.lsp.ModelNotificationLevel;
import dk.gtz.graphedit.spi.ILanguageServer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.yalibs.yafunc.IRunnable1;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class TextGrpcLsp implements ILanguageServer {
    private final Logger logger = LoggerFactory.getLogger(TextGrpcLsp.class);
	private final LanguageServerStub stub;
	private final Empty empty;
	private final int port; // TODO: Launch the program

	public TextGrpcLsp() {
		this("0.0.0.0", new Random().nextInt(5000, 6000));
	}

	public TextGrpcLsp(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build(), port);
	}

	public TextGrpcLsp(Channel channel, int port) {
		stub = LanguageServerGrpc.newStub(channel);
		empty = Empty.newBuilder().build();
		this.port = port;
	}

	@Override
	public String getLanguageName() {
		try {
			var so = new SingleResponseStreamObserver<ServerInfo>();
			stub.getServerInfo(empty, so);
			so.await();
			return so.get().getLanguage();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getServerName() {
		try {
			var so = new SingleResponseStreamObserver<ServerInfo>();
			stub.getServerInfo(empty, so);
			so.await();
			return so.get().getName();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getServerVersion() {
		try {
			var so = new SingleResponseStreamObserver<ServerInfo>();
			stub.getServerInfo(empty, so);
			so.await();
			return so.get().getSemanticVersion();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(File projectFile, IBufferContainer bufferContainer) {

	}

	@Override
	public void start() {
		// TODO: Start the language server in a thread
	}

	@Override
	public Collection<ModelLint> getDiagnostics() {
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

	@Override
	public void addDiagnosticsCallback(IRunnable1<Collection<ModelLint>> callback) {
		stub.getDiagnostics(empty, new StreamObserver<>() {
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
		stub.getNotifications(empty, new StreamObserver<>() {
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
		stub.getProgress(empty, new StreamObserver<>() {
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
