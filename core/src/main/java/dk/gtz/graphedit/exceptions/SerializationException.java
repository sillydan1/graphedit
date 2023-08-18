package dk.gtz.graphedit.exceptions;

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

