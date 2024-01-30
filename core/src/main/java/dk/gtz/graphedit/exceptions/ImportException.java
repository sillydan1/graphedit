package dk.gtz.graphedit.exceptions;

/**
 * An error occurred during importing from a non-graphedit format.
 */
public class ImportException extends RuntimeException {
    /**
     * Construct a new instance with a message
     * @param message The detail message
     */
    public ImportException(String message) {
        super(message);
    }

    /**
     * Construct a new instance with a message and a cause
     * @param cause The cause
     */
    public ImportException(Throwable cause) {
        super(cause);
    }
}
