package dk.gtz.graphedit.view.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

public class GlobFileMatcher {
    private final List<PathMatcher> matchers;

    public GlobFileMatcher(Path globIgnoreRulesFilePath) throws IOException {
	var lines = Files.readAllLines(globIgnoreRulesFilePath);
	matchers = new ArrayList<>(lines.size());
	for(var line : lines) {
	    if(line.isEmpty())
		continue;
	    if(line.trim().startsWith("#"))
		continue;
	    matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + line.trim()));
	}
    }

    public GlobFileMatcher() {
	matchers = new ArrayList<>();
    }

    public boolean matches(Path filePath) {
	return matchers.stream().anyMatch(m -> m.matches(filePath));
    }
}

