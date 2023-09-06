package dk.gtz.graphedit.exceptions;

/**
 * An error occurred during serialization of something
 */
public class SerializationException extends RuntimeException {
    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable inner) {
        super(message, inner);
    }

    public SerializationException(Throwable inner) {
        super(inner);
    }
}

