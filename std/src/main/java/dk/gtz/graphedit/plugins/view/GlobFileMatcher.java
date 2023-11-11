package dk.gtz.graphedit.plugins.view;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * A file matcher abstraction that matches glob-patterns specified from a provided file.
 *
 * An example could be the following file:
 * <pre>
 * {@code
 * .git
 * *.ignore
 * }
 * </pre>
 * This will ignore the {@code .git} folder as well as all files ending with the {@code .ignore} extension
 */
public class GlobFileMatcher {
    private final List<PathMatcher> matchers;

    /**
     * Constructs a new {@code GlobFileMatcher} instance based on a root matching path and a file containing glob-style expressions
     * @param rootDirPath the directory to match for globs in. This will be prepended to all glob loaded rules
     * @param globIgnoreRulesFilePath path to a file containing glob rules
     * @throws IOException if an OS error occurs when reading the provided glob ignore rules-file
     */
    public GlobFileMatcher(Path rootDirPath, Path globIgnoreRulesFilePath) throws IOException {
	var lines = Files.readAllLines(globIgnoreRulesFilePath);
	matchers = new ArrayList<>(lines.size());
	for(var line : lines) {
	    if(line.isEmpty())
		continue;
	    if(line.trim().startsWith("#"))
		continue;
	    addGlobPattern(rootDirPath, line);
	}
    }

    /**
     * Construcs a new {@code GlobFileMatcher} with no matchers
     */
    public GlobFileMatcher() {
	matchers = new ArrayList<>();
    }

    /**
     * Add an additional glob pattern to the list of matchers
     * @param rootDirPath the directory to match for globs in. This will be prepended to the provided pattern
     * @param globPattern the pattern to match for, e.g. {@code "*.ignore"}
     */
    public void addGlobPattern(Path rootDirPath, String globPattern) {
	matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + rootDirPath.toString() + "/" + globPattern.trim()));
    }

    /**
     * Check if any of the glob rules matches the provided path
     * @param filePath the path to match for
     * @return {@code true} if any of the registered glob patterns matches the provided filepath
     */
    public boolean matches(Path filePath) {
	return matchers.stream().anyMatch(m -> m.matches(filePath));
    }
}

