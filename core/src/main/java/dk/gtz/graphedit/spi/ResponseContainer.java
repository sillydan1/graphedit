package dk.gtz.graphedit.spi;

import java.util.Optional;

public class ResponseContainer<T> {
	private Optional<T> result;
	private Optional<Throwable> error;

	public ResponseContainer() {
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
