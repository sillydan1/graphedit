package dk.gtz.graphedit.exceptions;

/**
 * An error occurred during exporting to a non-graphedit format.
 */
public class ExportException extends RuntimeException {
    /**
     * Construct a new instance with a message
     * @param message The detail message
     */
    public ExportException(String message) {
        super(message);
    }

    /**
     * Construct a new instance with a message and a cause
     * @param cause The cause
     */
    public ExportException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a new instance with a message and a cause
     * @param message The detail message
     * @param cause The cause
     */
    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
