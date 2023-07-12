package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Map;

public record ModelProject(
		Map<String,String> metadata, 
		String name,
		String rootDirectory, // TODO: This should be removed, or at least be considered relative to the project file
		List<String> excludeFiles
		// What syntax to use for each file (maybe)
		) {}

