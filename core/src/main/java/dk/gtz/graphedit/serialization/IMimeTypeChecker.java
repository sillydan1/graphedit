package dk.gtz.graphedit.serialization;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for MIME type detectors
 */
public interface IMimeTypeChecker {
    /**
     * Get the MIME type of some file path.
     * @param path a file path
     * @return MIME type string. {@code null} if the mime type is not recognized
     * @throws IOException if the path does not point to a proper file
     */
    String getMimeType(Path path) throws IOException;
    /**
     * Get the MIME type of some file path string.
     * @param filePath a file path string
     * @return MIME type string. {@code null} if the mime type is not recognized
     * @throws IOException if the path does not point to a proper file
     */
    String getMimeType(String filePath) throws IOException;
}

