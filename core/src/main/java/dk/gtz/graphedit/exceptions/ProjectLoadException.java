package dk.gtz.graphedit.exceptions;

/**
 * An error occurred during loading a graphedit project
 */
public class ProjectLoadException extends RuntimeException {
    public ProjectLoadException(String message) {
        super(message);
    }
}

