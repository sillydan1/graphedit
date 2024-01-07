package dk.gtz.graphedit.plugins.syntaxes.text.lsp;

import java.util.Optional;

public class SyncSingleResponseContainer<T> {
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
