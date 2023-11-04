package dk.gtz.graphedit.spi;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import javafx.scene.Node;

public interface IEditorActions {
    /**
     * Exit the application
     */
    void quit();
    /**
     * Save a specific buffer to disk.
     * @param buffer The buffer to save
     * @param file The file to save to
     */
    void save(ViewModelProjectResource buffer, File file);
    /**
     * Save a specific buffer to disk.
     * @param buffer The buffer to save
     */
    void saveAs(ViewModelProjectResource buffer);
    /**
     * Save the currently opened project and all opened buffers to disk.
     * This will fire any listeners added in {@link addSaveListener}
     */
    void saveAll();
    void addSaveListener(Runnable runner);
    void removeSaveListener(Runnable runner);
    /**
     * Save a specific project to disk at a specified path
     * @param project The project object to save
     * @param projectFilePath The path to save to
     */
    void saveProject(ModelProject project, Path projectFilePath);
    void openProject(File projectPath);
    void openBuffer(File bufferFile);
    void openBuffer(ViewModelProjectResource buffer);
    void closeBuffer(ViewModelProjectResource buffer);
    Optional<File> createNewFile();
    void toggleTheme();
    void openModal(String title);
    void openModal(URL fxmlFile, String title);
    void openModal(Node node, String title);
    void undo();
    void redo();
    void executeRunTarget(ViewModelRunTarget runtarget);
    Optional<Boolean> showConfirmDialog(String title, String question);
    ViewModelEditorSettings loadEditorSettings();
    void saveEditorSettings(ViewModelEditorSettings settings);
}

