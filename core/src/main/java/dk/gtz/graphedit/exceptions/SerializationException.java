package dk.gtz.graphedit.exceptions;

/**
 * An error occurred during serialization of something
 */
public class SerializationException extends RuntimeException {
    /**
     * Construct a new instance
     * @param message The detail message
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     * Construct a new instance
     * @param message The detail message
     * @param inner The throwable that caused this
     */
    public SerializationException(String message, Throwable inner) {
        super(message, inner);
    }

    /**
     * Construct a new instance
     * @param inner The throwable that caused this
     */
    public SerializationException(Throwable inner) {
        super(inner);
    }
}
