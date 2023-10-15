package dk.gtz.graphedit.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * On-disk file structure for a project file
 *
 * Not to be confused with {@link ModelProjectResource}, this class represents a graphedit project.
 */
public record ModelProject(Map<String,String> metadata,  String name, List<String> excludeFiles, List<ModelRunTarget> runTargets) {
	public ModelProject(String name) {
		this(new HashMap<String,String>(), name, new ArrayList<String>(), new ArrayList<ModelRunTarget>());
	}
}

