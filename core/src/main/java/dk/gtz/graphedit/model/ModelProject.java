package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Map;

// TODO: I think this is no longer needed and that ModelProjectResource.java replaces this. Investigate and remove if possible. Otherwise, write a javadoc
public record ModelProject(
		Map<String,String> metadata, 
		String name,
		List<String> excludeFiles,
		List<ModelRunTarget> runTargets
		// What syntax to use for each file (maybe)
		) {}

