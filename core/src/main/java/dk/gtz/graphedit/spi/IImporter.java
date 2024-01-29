package dk.gtz.graphedit.spi;

import java.nio.file.Path;
import java.util.List;

import dk.gtz.graphedit.exceptions.ImportException;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.model.ModelProjectResource;

/**
 * An importer interface for importing / converting non-graphedit projects to graphedit format.
 *
 * Note that none of the functions should actually save to disk.
 */
public interface IImporter {
	public record FiletypesFilter(String description, List<String> extensions) {}
	public record ImportProjectResult(Path newFileLocation, ModelProject newProject) {}
	public record ImportResult(Path newFileLocation, ModelProjectResource newModel) {}

	/**
	 * Get the supported filetypes.
	 * @return A filetypes filter object with a description and at least one extension
	 */
	default FiletypesFilter getFiletypesFilter() {
		return new FiletypesFilter("All", List.of("*.*"));
	}

	/**
	 * Get the target project type that the importer can import.
	 * @return A string name of the type of project that this importer can import.
	 */
	String getName();

	/**
	 * Converts all files in a project and return the new model.
	 * @param importPath Path to either the folder, or project file to convert.
	 * @return A new project in graphedit format.
	 * @throws ImportException if something went wrong during the import.
	 */
	ImportProjectResult importProject(Path importPath) throws ImportException;

	/**
	 * Converts a single model file to graphedit format.
	 * @param importPath Path to either a folder or file to convert.
	 * @return A new project resource in graphedit format.
	 * @throws ImportException if something went wrong during the import.
	 */
	ImportResult importFile(Path importPath) throws ImportException;
}
