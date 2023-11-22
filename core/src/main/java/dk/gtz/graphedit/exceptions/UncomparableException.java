package dk.gtz.graphedit.exceptions;

/**
 * An error occurred during a compare
 */
public class UncomparableException extends RuntimeException {
    /**
     * Construct a new instance
     * @param message The detail message
     */
    public UncomparableException(String message) {
        super(message);
    }

    /**
     * Construct a new instance
     * @param message The detail message
     * @param inner The throwable that caused this
     */
    public UncomparableException(String message, Throwable inner) {
        super(message, inner);
    }

    /**
     * Construct a new instance
     * @param inner The throwable that caused this
     */
    public UncomparableException(Throwable inner) {
        super(inner);
    }
}
