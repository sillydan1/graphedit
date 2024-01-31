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
	public record FiletypesFilter(String description, List<String> extensions) {}
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
	 * Converts a series of model files to graphedit format and imports them into the currently open project.
	 * @param importPaths List of path to either a folder or file to convert.
	 * @return A new project resource in graphedit format.
	 * @throws ImportException if something went wrong during the import.
	 */
	List<ImportResult> importFiles(List<File> importPaths) throws ImportException;
}
