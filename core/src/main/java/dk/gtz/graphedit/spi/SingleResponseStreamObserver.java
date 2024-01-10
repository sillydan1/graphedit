package dk.gtz.graphedit.spi;

import java.util.concurrent.CountDownLatch;

import io.grpc.stub.StreamObserver;

public class SingleResponseStreamObserver<T> implements StreamObserver<T> {
	private final ResponseContainer<T> container;
	private final CountDownLatch latch;

	public SingleResponseStreamObserver() {
		container = new ResponseContainer<>();
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
