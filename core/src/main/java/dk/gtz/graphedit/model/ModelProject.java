package dk.gtz.graphedit.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * On-disk file structure for a project file
 */
public record ModelProject(
		Map<String,String> metadata, 
		String name,
		List<String> excludeFiles,
		List<ModelRunTarget> runTargets
		// What syntax to use for each file (maybe)
		) {
	public ModelProject(String name) {
		this(
				new HashMap<String,String>(),
				name,
				new ArrayList<String>(),
				new ArrayList<ModelRunTarget>()
			);
	}
}

