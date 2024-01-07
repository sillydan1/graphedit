package dk.gtz.graphedit.plugins.syntaxes.text.lsp;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import dk.gtz.graphedit.Empty;
import dk.gtz.graphedit.LanguageServerGrpc;
import dk.gtz.graphedit.LanguageServerGrpc.LanguageServerStub;
import dk.gtz.graphedit.ServerInfo;
import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgress;
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.spi.ILanguageServer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.yalibs.yafunc.IRunnable1;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

public class TextGrpcLsp implements ILanguageServer {
	private final LanguageServerStub stub;
	private final Empty empty;

	private static class SyncSingleResponseContainer<T> {
		private Optional<T> result;
		private Optional<Throwable> error;

		public SyncSingleResponseContainer() {
			result = Optional.empty();
			error = Optional.empty();
		}

		public void set(T e) {
			result = Optional.of(e);
		}

		public void setError(Throwable e) {
			error = Optional.of(e);
		}

		public T get() {
			if(error.isPresent())
				throw new RuntimeException(error.get());
			if(result.isEmpty())
				throw new RuntimeException("no result and also no error");
			return result.get();
		}
	}

	private static class SingleResponseStreamObserver<T> implements StreamObserver<T> {
		private final SyncSingleResponseContainer<T> container;
		private final CountDownLatch latch;

		public SingleResponseStreamObserver() {
			container = new SyncSingleResponseContainer<>();
			latch = new CountDownLatch(1);
		}

		@Override
		public void onNext(T value) {
			container.set(value);
		}

		@Override
		public void onError(Throwable t) {
			container.setError(t);
			latch.countDown();
		}

		@Override
		public void onCompleted() {
			latch.countDown();
		}

		public T get() {
			return container.get();
		}

		public void await() throws InterruptedException {
			latch.await();
		}
	}

	public TextGrpcLsp(Channel channel) {
		stub = LanguageServerGrpc.newStub(channel);
		empty = Empty.newBuilder().build();
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

	}

	@Override
	public Collection<ModelLint> getDiagnostics() {
		return List.of();
	}

	@Override
	public void addDiagnosticsCallback(IRunnable1<Collection<ModelLint>> callback) {

	}

	@Override
	public void addNotificationCallback(IRunnable1<ModelNotification> callback) {

	}

	@Override
	public void addProgressCallback(IRunnable1<ModelLanguageServerProgress> callback) {

	}
}
