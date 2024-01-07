package dk.gtz.graphedit.plugins.syntaxes.text.lsp;

import java.util.concurrent.CountDownLatch;

import io.grpc.stub.StreamObserver;

public class SingleResponseStreamObserver<T> implements StreamObserver<T> {
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
