package dk.gtz.graphedit.tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.BuildConfig;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.model.ModelEditorSettings;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.view.EditorController;
import dk.gtz.graphedit.view.IRestartableApplication;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ICloseable;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import dk.yalibs.yafunc.IFunction2;
import dk.yalibs.yastreamgobbler.StreamGobbler;
import dk.yalibs.yaundo.IUndoSystem;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * The central point of all scriptable editor actions. These actions can help you modify the editor to your hearts content
 * 
 * Plans are in the works to have scripting language bindings to these functions
 */
public class EditorActions {
    private static final Logger logger = LoggerFactory.getLogger(EditorActions.class);
    private static List<Runnable> saveListeners = new ArrayList<>();

    /**
     * Will immediately quit the application with no hesitation.
     */
    public static void quit() {
        Platform.exit();
    }


    public static void addSaveListener(Runnable runner) {
        saveListeners.add(runner);
    }

    public static boolean removeSaveListener(Runnable runner) {
        return saveListeners.remove(runner);
    }

    /**
     * Saves the currently opened project to disk. Will prompt the user for a path to save to.
     */
    public static void saveAs() {
        var result = newFile();
        if(result.isEmpty()) {
            logger.warn("save action cancelled, will not save");
            return;
        }
        var project = DI.get(ViewModelProject.class);
        project.rootDirectory().set(result.get().getParent());
        project.isSavedInTemp().set(false);
        var editorSettings = DI.get(ViewModelEditorSettings.class);
        editorSettings.lastOpenedProject().set(result.get().toString());
        EditorActions.saveEditorSettings(editorSettings);
        EditorActions.save();
    }

    /**
     * Saves the currently opened project to disk.
     * Will not throw any exceptions, but errors may be logged if something went awry.
     */
    public static void save() {
        var project = DI.get(ViewModelProject.class);
        if(project.isSavedInTemp().get()) {
            saveAs();
            return;
        }
        var serializer = DI.get(IModelSerializer.class);
        var buffers = DI.get(IBufferContainer.class).getBuffers().entrySet();
        var editorSettings = DI.get(ViewModelEditorSettings.class);
        var lastOpenedProject = editorSettings.lastOpenedProject();
        EditorActions.saveProject(project.toModel(), Path.of(lastOpenedProject.get()));
        logger.trace("save starting");
        buffers.parallelStream().forEach((buffer) -> {
            try {
                var project2 = DI.get(ViewModelProject.class);
                var filePath = project2.rootDirectory().getValueSafe() + File.separator + buffer.getKey();
                logger.trace("saving file {}", filePath);
                var model = buffer.getValue().toModel();
                var serializedModel = serializer.serialize(model);
                var p = Paths.get(filePath);
                Files.createDirectories(p.getParent());
                Files.write(p, serializedModel.getBytes());
            } catch (SerializationException e) {
                logger.error("failed to serialize model '{}' reason: {}", buffer.getKey(), e.getMessage());
            } catch (IOException e) {
                logger.error("failed to save file '{}' reason: {}", buffer.getKey(), e.getMessage());
            }
        });
        Platform.runLater(() -> saveListeners.forEach(Runnable::run));
        Toast.success("save complete");
        logger.trace("save complete");
    }

    /**
     * Loads the global editor settings file and returns a viewmodel version of it.
     *
     * Note. Will provide a default setttings object if the file could not be loaded.
     * Note. Will use the {@see DI} injected {@see IModelSerializer} to serialize.
     *
     * @return the viewmodel representation of the editor settings
     */
    public static ViewModelEditorSettings loadEditorSettings() {
        try {
            var serializer = DI.get(IModelSerializer.class);
            var fileToLoad = ModelEditorSettings.getEditorSettingsFile();
            if(!fileToLoad.toFile().exists()) {
                var data = serializer.serializeEditorSettings(new ModelEditorSettings());
                Files.createDirectories(fileToLoad.getParent());
                Files.write(fileToLoad, data.getBytes());
            }
            return new ViewModelEditorSettings(serializer.deserializeEditorSettings(fileToLoad.toFile()));
        } catch(Exception e) {
            logger.warn("could not load or create editor settings file, will return to default settings", e);
            return new ViewModelEditorSettings(new ModelEditorSettings());
        }
    }

    /**
     * Save the global editor settings to disk. The location of the file is determined by {@see ModelEditorSettings#getEditorSettingsFile}
     *
     * Note. Will use the {@see DI} injected {@see IModelSerializer} to serialize.
     *
     * @param settings the settings object to save to disk
     */
    public static void saveEditorSettings(ViewModelEditorSettings settings) {
        try {
            var serializer = DI.get(IModelSerializer.class);
            var data = serializer.serializeEditorSettings(new ModelEditorSettings(settings));
            var fileToSave = ModelEditorSettings.getEditorSettingsFile();
            if(!fileToSave.toFile().exists())
                Files.createDirectories(fileToSave.getParent());
            Files.write(fileToSave, data.getBytes());
            logger.info("saved settings file {}", fileToSave.toString());
        } catch(Exception e) {
            logger.error("could not save editor settings file", e);
        }
    }

    /**
     * Will attempt to open the project file provided
     *
     * Note. Will not prompt to save unsaved changes.
     *
     * @param projectPath the project file to load
     */
    public static void openProject(File projectPath) {
        try {
            logger.trace("loading project {}", projectPath.getAbsolutePath().toString());
            var serializer = DI.get(IModelSerializer.class);
            serializer.deserializeProject(projectPath);
            var settings = DI.get(ViewModelEditorSettings.class);
            settings.lastOpenedProject().set(projectPath.getAbsolutePath().toString());
            if(!settings.recentProjects().contains(projectPath.getAbsolutePath().toString()))
                settings.recentProjects().add(projectPath.getAbsolutePath().toString());
            saveEditorSettings(settings);
            DI.get(IRestartableApplication.class).restart();
        } catch (Exception e) {
            logger.error("failed opening project: {}", e.getMessage(), e);
        }
    }

    /**
     * Save a model project to a specific file
     * @param project
     * @param projectFilePath
     */
    public static void saveProject(ModelProject project, Path projectFilePath) {
        try {
            var serializer = DI.get(IModelSerializer.class);
            var data = serializer.serialize(project);
            if(projectFilePath.toFile().isDirectory())
                projectFilePath.resolve(project.name() + ".json");
            Files.write(projectFilePath, data.getBytes());
            logger.trace("saved project {} successfully", project.name());
        } catch(Exception e) {
            logger.error("failed saving project: {}", e.getMessage(), e);
        }
    }

    public static void saveProject() {
        var project = DI.get(ViewModelProject.class);
        if(project.isSavedInTemp().get()) {
            save();
            return;
        }
        var editorSettings = DI.get(ViewModelEditorSettings.class);
        var lastOpenedProject = editorSettings.lastOpenedProject();
        saveProject(project.toModel(), Path.of(lastOpenedProject.get()));
    }

    /**
     * Will open the project picker dialogue
     * @param window the associated window
     * @return Optionally a file if one was chosen otherwise empty
     */
    public static Optional<File> openProjectPicker(Window window) {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Json files", "*.json"),
                new ExtensionFilter("Any files", "*.*")
                );
        fileChooser.setTitle("Open Graphedit Project");
        return Optional.ofNullable(fileChooser.showOpenDialog(window));
    }

    /**
     * Will open a "save as" file picker OS-native dialogue
     * @return Optionally a file if one was chosen otherwise empty
     */
    public static Optional<File> newFile() {
        var fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Path.of(DI.get(ViewModelProject.class).rootDirectory().get()).toFile());
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("json files", "*.json"));
        fileChooser.setTitle("New file");
        return Optional.ofNullable(fileChooser.showSaveDialog(DI.get(Window.class)));
    }

    /**
     * Toggle between light and dark theme for the editor
     */
    public static void toggleTheme() {
        var useLightTheme = DI.get(ViewModelEditorSettings.class).useLightTheme();
        useLightTheme.set(!useLightTheme.get());
    }

    /**
     * Open the {@see ViewModelEditorSettings} editor modal pane
     */
    public static void openEditorSettings() {
        openModal("EditorSettings.fxml", "Editor Settings");
    }

    public static void openProjectSettings() {
        openModal("ProjectSettings.fxml", "Project Settings");
    }

    /**
     * Open the {@see ViewModelRunTarget} editor modal pane
     */
    public static void openRunTargetsEditor() {
        openModal("RunTargetsEditor.fxml", "Runtarget Settings");
    }

    /**
     * Open the {@see SearchPaneController} modal pane
     */
    public static void openSearchPane() {
        openModal("SearchPane.fxml", "Search");
    }

    /**
     * Opens the about info pane modal
     */
    public static void openAboutPane() {
        IFunction2<Text,String,String> createText = (String text, String style) -> {
            var returnValue = new Text(text);
            returnValue.getStyleClass().add(style);
            return returnValue;
        };
        var aboutNode = new VBox(
            createText.run("Graphedit", Styles.TITLE_1),
            createText.run(BuildConfig.APP_VERSION, Styles.TITLE_3),
            new Text("")); // TODO: write a description about graphedit... or something
        openModal(aboutNode, "About");
    }

    /**
     * Opens an injected modal
     * @param node the modal to show
     */
    public static void openModal(Node node, String title) {
        var modalPane = DI.get(ModalPane.class);
        var centerPane = new StackPane(node);
        var showPane = new BorderPane(centerPane);
        centerPane.setPadding(new Insets(20));
        centerPane.setMaxSize(450, 450);
        showPane.setMaxSize(450, 450);
        Styles.addStyleClass(showPane, Styles.BG_DEFAULT);
        showPane.getStyleClass().add("modal-rounded");
        var titleLabel = new Label(title);
        Styles.addStyleClass(titleLabel, Styles.TITLE_2);
        var titleBox = new HBox(titleLabel);
        titleBox.setPadding(new Insets(5));
        titleBox.setAlignment(Pos.CENTER);
        showPane.setTop(titleBox);
        modalPane.show(showPane);
        node.requestFocus();
    }

    /**
     * Opens an FXML based modal
     * @param fxmlFile the .fxml file to open, must be from the perspective of {@see EditorController}
     */
    public static void openModal(String fxmlFile, String title) {
        try {
            var loader = new FXMLLoader(EditorController.class.getResource(fxmlFile));
            var content = (Node)loader.load();
            openModal(content, title);
            var controller = loader.getController();
            if(controller != null && controller instanceof IFocusable focusableController)
                focusableController.focus();
            if(controller != null && controller instanceof ICloseable closableController) {
                var modalPane = DI.get(ModalPane.class);
                closableController.onClose(modalPane::hide);
            }
        } catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Perform an undo action
     */
    public static void undo() {
        DI.get(IUndoSystem.class).undo();
    }

    /**
     * Perform a redo action
     */
    public static void redo() {
        DI.get(IUndoSystem.class).redo();
    }

    /**
     * Execute the provided runtarget
     *
     * Note. Will provide the action with all the default environment variables:
     *   PROJECT_NAME - the name of the currently open project
     *   PROJECT_DIR - the directory path to the currently open project
     * @param selectedRunTarget the runtarget to execute
     */
    public static void executeRunTarget(ViewModelRunTarget selectedRunTarget) {
        // NOTE: Does not change the MenuItem labels
        // NOTE: This is a blocking call
        try {
            if(selectedRunTarget.saveBeforeRun().get())
                save();
            var pb = new ProcessBuilder();
            if(selectedRunTarget.runAsShell().get()) {
                var sb = new StringBuilder(selectedRunTarget.command().get());
                for(var argument : selectedRunTarget.arguments())
                    sb.append(" ").append(argument.getValueSafe());
                pb.command("/bin/sh", "-c", sb.toString());
            } else {
                pb.command(selectedRunTarget.command().get());
                for(var argument : selectedRunTarget.arguments())
                    pb.command().add(argument.get());
            }
            var env = pb.environment();
            var project = DI.get(ViewModelProject.class);
            env.put("PROJECT_NAME", project.name().getValueSafe());
            env.put("PROJECT_DIR", project.rootDirectory().getValueSafe());
            for(var e : selectedRunTarget.environment())
                env.put(e.key().get(), e.value().get());
            if(!selectedRunTarget.currentWorkingDirectory().get().isEmpty())
                pb.directory(new File(selectedRunTarget.currentWorkingDirectory().get()));
            pb.redirectErrorStream(true);
            var p = pb.start();
            var outputGobbler = new StreamGobbler(p.getInputStream(), logger::info);
            new Thread(outputGobbler).start();
            p.waitFor();
        } catch(InterruptedException e) {
            logger.warn("runtarget was interrupted", e);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            logger.trace("runtarget finished");
        }
    }

    /**
     * Will prompt the user for a Confirm / Cancel action
     * @param questionTitle prompt title
     * @param question the question to ask the user
     * @param window the parent window
     * @return {@code true} if the user selected the affirmative action, {@code false} if user selected the negative action or {@code Optional.empty} if the prompt was closed with no action selected
     */
    public static Optional<Boolean> showConfirmDialog(String questionTitle, String question, Window window) {
        var alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(questionTitle);
        alert.setHeaderText(question);
        var yesBtn = new ButtonType("Confirm", ButtonData.YES);
        var noBtn = new ButtonType("Cancel", ButtonData.NO);
        alert.getButtonTypes().setAll(yesBtn, noBtn);
        alert.initOwner(window);
        var result = alert.showAndWait();
        if(result.isEmpty())
            return Optional.empty();
        if(result.get().getButtonData().equals(ButtonData.NO))
            return Optional.of(false);
        return Optional.of(true);
    }
}

