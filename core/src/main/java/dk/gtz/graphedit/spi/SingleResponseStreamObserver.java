package dk.gtz.graphedit.spi;

import java.util.concurrent.CountDownLatch;

import io.grpc.stub.StreamObserver;

/**
 * Utility class for capturing a single gRPC response and returning it.
 *
 * Example Usage:
 * 
 * <pre>
 * {@code
 * try {
 * 	var so = new SingleResponseStreamObserver<GRPCObject>();
 * 	stub.get().getValue(empty, so);
 * 	so.await();
 * 	return so.get();
 * } catch (InterruptedException e) {
 * 	// handle e
 * }
 * }
 * </pre>
 * 
 * @param <T> The type of return value
 */
public class SingleResponseStreamObserver<T> implements StreamObserver<T> {
	private final ResponseContainer<T> container;
	private final CountDownLatch latch;

	/**
	 * Constructs a new SingleResponseStreamObserver instance
	 */
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

	/**
	 * Get the returned value.
	 * 
	 * @return An instance of T
	 * @throws RuntimeException if an error had occurred or if the value is not
	 *                          present
	 */
	public T get() {
		return container.get();
	}

	/**
	 * Wait for the request to complete.
	 * 
	 * @throws InterruptedException if the current thread is interrupted while
	 *                              waiting
	 */
	public void await() throws InterruptedException {
		latch.await();
	}
}
