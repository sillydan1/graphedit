package dk.gtz.graphedit.view;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.view.util.PlatformUtils;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EditorController {
    private final Logger logger = LoggerFactory.getLogger(EditorController.class);
    @FXML
    private ProjectFilesViewController filePaneController;
    @FXML
    private VBox menubarTopBox;
    @FXML
    private StackPane root;
    @FXML
    private Menu runTargetsMenu;
    @FXML
    private MenuItem runTargetMenuItem;
    private Thread runTargetThread;
    private Optional<ViewModelRunTarget> selectedRunTarget;

    @FXML
    private void initialize() {
	selectedRunTarget = Optional.empty();
	runTargetThread = new Thread(this::runTarget);
	initProjectMenu();
	hideTopbarOnSupportedPlatforms();
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

    @FXML
    private void toggleTheme() {
	EditorActions.toggleTheme();
    }

    @FXML
    private void openSettingsEditor() {
	EditorActions.openSettingsEditor();
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
    private void loadProject() {
	logger.warn("still work in progress");
    }

    @FXML
    private void quit() {
        var alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Save and Exit?");
        alert.setHeaderText("Save your changes before you exit?");
        var yesBtn = new ButtonType("Save and exit", ButtonData.YES);
        var noBtn = new ButtonType("Exit without saving", ButtonData.NO);
        alert.getButtonTypes().setAll(yesBtn, noBtn);
        alert.initOwner(root.getScene().getWindow());
        var result = alert.showAndWait();
        if(result.isEmpty()) {
            logger.warn("cancelling quit action");
            return;
        }
        if(result.get().getButtonData().equals(ButtonData.NO)) {
	    EditorActions.quit();
            return;
        }
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
	EditorActions.executeRunTarget(selectedRunTarget.get());
	Platform.runLater(() -> runTargetMenuItem.setText("Start Selected RunTarget"));
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
    private void openProject() {
	var file = EditorActions.openProjectPicker(menubarTopBox.getScene().getWindow());
	if(file.isPresent())
	    EditorActions.openProject(file.get());
    }
}

