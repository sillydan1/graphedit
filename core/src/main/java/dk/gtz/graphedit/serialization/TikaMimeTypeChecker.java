package dk.gtz.graphedit.serialization;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.tika.Tika;

public class TikaMimeTypeChecker implements IMimeTypeChecker {
    private Tika tika;

    public TikaMimeTypeChecker() {
	tika = new Tika();
    }

    @Override
    public String getMimeType(Path path) throws IOException {
	return tika.detect(path);
    }

    @Override
    public String getMimeType(String filePath) throws IOException {
	return getMimeType(Path.of(filePath));
    }
}

