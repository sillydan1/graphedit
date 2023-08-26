package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Map;

public record ModelProject(
		Map<String,String> metadata, 
		String name,
		List<String> excludeFiles,
		List<ModelRunTarget> runTargets
		// What syntax to use for each file (maybe)
		) {}

