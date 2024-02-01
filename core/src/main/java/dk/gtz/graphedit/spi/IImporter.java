package dk.gtz.graphedit.spi;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import dk.gtz.graphedit.exceptions.ImportException;
import dk.gtz.graphedit.model.ModelProjectResource;

/**
 * An importer interface for importing / converting non-graphedit projects to graphedit format.
 *
 * Note that none of the functions should actually save to disk.
 */
public interface IImporter {
	/**
	 * A filter object for representing supported filetypes.
	 * @param description A description of the filetypes
	 * @param extensions A list of file extensions of the format "*.ext"
	 */
	public record FiletypesFilter(String description, List<String> extensions) {}


	/**
	 * A result object for representing the result of an import.
	 * @param newFileLocation The path to the new file Location
	 * @param newModel The new model resource
	 */
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
	 * This will be used in the importer drop-down UI.
	 * @return A string name of the type of project that this importer can import.
	 */
	String getName();

	/**
	 * Converts a series of model files to graphedit format and imports them into the currently open project.
	 * @param importPaths List of path to either a folder or file to convert.
	 * @return A new project resource in graphedit format.
	 * @throws ImportException if something went wrong during the import.
	 */
	List<ImportResult> importFiles(List<File> importPaths) throws ImportException;
}
