package dk.gtz.graphedit.exceptions;

/**
 * An error occurred during loading a graphedit project
 */
public class ProjectLoadException extends RuntimeException {
	/**
	 * Construct a new instance with a message
	 * 
	 * @param message The detail message
	 */
	public ProjectLoadException(String message) {
		super(message);
	}
}
