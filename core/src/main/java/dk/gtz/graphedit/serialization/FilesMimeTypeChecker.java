package dk.gtz.graphedit.serialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesMimeTypeChecker implements IMimeTypeChecker {
    @Override
    public String getMimeType(Path path) throws IOException {
	return Files.probeContentType(path);
    }

    @Override
    public String getMimeType(String filePath) throws IOException {
	return getMimeType(Path.of(filePath));
    }
}

