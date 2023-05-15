package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Map;

public record Project(
		Map<String,String> metadata, 
		String rootDirectory, 
		List<String> excludeFiles
		// What syntax to use for each file (maybe)
		) {}

