package dk.gtz.graphedit.spi;

import java.nio.file.Path;

import dk.gtz.graphedit.exceptions.ExportException;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;

/**
 * Interface for exporting a project resource to a non-graphedit file format.
 */
public interface IExporter {
	/**
	 * Exports the given resource to the given file path.
	 *
	 * Note that this will write the new file to the file system.
	 * @param resource The resource to export.
	 * @param newFilePath The file path to export to.
	 * @throws ExportException If the export fails.
	 */
	void exportFile(ViewModelProjectResource resource, Path newFilePath) throws ExportException;

	/**
	 * Returns the name of the exporter.
	 * @return The name of the exporter
	 */
	String getName();
}
