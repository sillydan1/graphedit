package dk.gtz.graphedit.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * On-disk file structure for a project file
 *
 * Not to be confused with {@link ModelProjectResource}, this class represents a graphedit project.
 * @param metadata A map of generic string-encoded metadata, useful for syntax=specific data
 * @param name The name of the project
 * @param excludeFiles A list of files to exclude from the project, can be glob-specifications
 * @param runTargets A list of {@link ModelRunTarget} associated with this project
 */
public record ModelProject(
		Map<String,String> metadata,
		String name,
		List<String> excludeFiles,
		List<ModelRunTarget> runTargets) {
	/**
	 * Constructs a new project model with a specified name
	 * @param name The name of the project
	 */
	public ModelProject(String name) {
		this(new HashMap<String,String>(), name, new ArrayList<String>(), new ArrayList<ModelRunTarget>());
	}
}
