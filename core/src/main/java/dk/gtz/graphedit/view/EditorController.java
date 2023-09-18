package dk.gtz.graphedit.view;

import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.view.util.HeightDragResizer;
import dk.gtz.graphedit.view.util.PlatformUtils;
import dk.gtz.graphedit.view.util.WidthDragResizer;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class EditorController {
    private final Logger logger = LoggerFactory.getLogger(EditorController.class);
    @FXML
    private ProjectFilesViewController filePaneController;
    @FXML
    private VBox menubarTopBox;
    @FXML
    private StackPane root;
    @FXML
    private BorderPane primaryBorderPane;
    @FXML
    private Menu runTargetsMenu;
    @FXML
    private MenuItem runTargetMenuItem;
    @FXML
    private Pane inspectorGroup;
    private Thread runTargetThread;
    private Optional<ViewModelRunTarget> selectedRunTarget;

    @FXML
    private void initialize() {
	selectedRunTarget = Optional.empty();
	runTargetThread = new Thread(this::runTarget);
	WidthDragResizer.makeResizableRight((Region)primaryBorderPane.getLeft());
	HeightDragResizer.makeResizableUp((Region)primaryBorderPane.getBottom());
	initProjectMenu();
	hideTopbarOnSupportedPlatforms();
	initInspectorPane();
    }

    private void initProjectMenu() {
	var project = DI.get(ViewModelProject.class);
	updateRunTargets();
	project.runTargets().addListener((e,o,n) -> updateRunTargets());
    }

    private void updateRunTargets() {
	runTargetsMenu.getItems().clear();
	var project = DI.get(ViewModelProject.class);
	runTargetsMenu.setDisable(project.runTargets().isEmpty());
	var toggleGroup = new ToggleGroup();
	for(var runTarget : project.runTargets()) {
	    var toggleItem = new RadioMenuItem(runTarget.name().get());
	    toggleItem.textProperty().bind(runTarget.name());
	    toggleItem.setOnAction(e -> selectedRunTarget = Optional.of(runTarget));
	    toggleItem.setToggleGroup(toggleGroup);
	    if(selectedRunTarget.isPresent() && selectedRunTarget.get() == runTarget)
		toggleGroup.selectToggle(toggleItem);
	    runTargetsMenu.getItems().add(toggleItem);
	}
    }

    private void hideTopbarOnSupportedPlatforms() {
        if (PlatformUtils.isSystemMenuBarSupported()) {
            menubarTopBox.setVisible(false);
            menubarTopBox.setManaged(false);
        }
    }

    private void initInspectorPane() {
	inspectorGroup.visibleProperty().bind(DI.get(ViewModelEditorSettings.class).showInspectorPane());
	inspectorGroup.managedProperty().bind(DI.get(ViewModelEditorSettings.class).showInspectorPane());
    }

    @FXML
    private void toggleTheme() {
	EditorActions.toggleTheme();
    }

    @FXML
    private void openSettingsEditor() {
	EditorActions.openEditorSettings();
    }

    @FXML
    private void openProjectEditor() {
	EditorActions.openProjectSettings();
    }

    @FXML
    private void undo() {
	EditorActions.undo();
    }

    @FXML
    private void redo() {
	EditorActions.redo();
    }

    @FXML
    private void save() {
	EditorActions.save();
    }

    @FXML
    private void saveAs() {
	EditorActions.saveAs();
    }

    @FXML
    private void loadProject() {
	logger.warn("still work in progress");
    }

    @FXML
    private void quit() {
	var w = DI.get(Window.class);
	var result = EditorActions.showConfirmDialog("Save and Exit?", "Save your changes before you exit?", w);
	if(result.isEmpty())
	    return;
        if(result.get())
	    EditorActions.save();
	EditorActions.quit();
    }

    @FXML
    private void featureHolder() throws Exception {
    }

    @FXML
    private void editRunTargets() {
	EditorActions.openRunTargetsEditor();
    }

    private void runTarget() {
        if(selectedRunTarget.isEmpty()) {
            logger.warn("No RunTarget selected");
            return;
        }
	Platform.runLater(() -> {
	    EditorActions.executeRunTarget(selectedRunTarget.get());
	    runTargetMenuItem.setText("Start Selected RunTarget");
	});
    }

    @FXML
    private void runSelectedRunTarget() {
	if(runTargetThread.isAlive()) {
	    runTargetThread.interrupt();
	} else {
	    runTargetThread = new Thread(this::runTarget);
	    runTargetThread.start();
	    runTargetMenuItem.setText("Stop Selected RunTarget");
	}
    }

    @FXML
    private void openAboutPane() {
	EditorActions.openAboutPane();
    }

    @FXML
    private void openSearchPane() {
	EditorActions.openSearchPane();
    }

    @FXML
    private void newFile() {
	filePaneController.createNewModelFile();
    }

    @FXML
    private void newProject() {
	var file = EditorActions.newFile();
	if(!file.isPresent())
	    return;
	// TODO: project data inspector / editor so people can change the project name later
	var modelProject = new ModelProject(PlatformUtils.removeFileExtension(file.get().getName()));
	EditorActions.saveProject(modelProject, Path.of(file.get().getAbsolutePath()));
	EditorActions.openProject(file.get());
    }

    @FXML
    private void openProject() {
	var w = DI.get(Window.class);
	var file = EditorActions.openProjectPicker(w);
	if(file.isPresent())
	    EditorActions.openProject(file.get());
    }
}

