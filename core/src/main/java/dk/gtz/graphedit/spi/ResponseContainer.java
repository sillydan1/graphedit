package dk.gtz.graphedit.spi;

import java.util.Optional;

/**
 * A container utility class that can hold either: nothing, valid value or an error.
 * @param <T> The type of the contained value
 */
public class ResponseContainer<T> {
	private Optional<T> result;
	private Optional<Throwable> error;

	/**
	 * Constructs a new container instance
	 */
	public ResponseContainer() {
		result = Optional.empty();
		error = Optional.empty();
	}

	/**
	 * Set the result value
	 * @param e the result value
	 */
	public void set(T e) {
		result = Optional.of(e);
	}

	/**
	 * Set the container to have an error.
	 * @param e the error to hold
	 */
	public void setError(Throwable e) {
		error = Optional.of(e);
	}

	/**
	 * Get the contained value.
	 * @return An instance of T
	 * @throws RuntimeException if an error had occurred or if the value is not present
	 */
	public T get() {
		if(error.isPresent())
			throw new RuntimeException(error.get());
		if(result.isEmpty())
			throw new RuntimeException("no result and also no error");
		return result.get();
	}
}
