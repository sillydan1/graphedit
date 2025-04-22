package dk.gtz.graphedit.viewmodel;

/**
 * Interface for closing actions
 */
public interface ICloseable {
	/**
	 * Set a function to handle the close event.
	 * 
	 * @param closer the function to call when {@link #close()} is called
	 */
	void onClose(Runnable closer);

	/**
	 * Close the unit. This will invoke the {@link #onClose(Runnable)} handler.
	 */
	void close();
}
