package dk.gtz.graphedit.view;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.view.util.PlatformUtils;
import dk.gtz.graphedit.view.util.PreferenceUtil;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

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
	var fileChooser = new FileChooser();
	fileChooser.setTitle("Open Project");
	var chosenFile = fileChooser.showOpenDialog(menubarTopBox.getScene().getWindow());
	if(chosenFile == null)
	    return;
	try {
	    logger.trace("loading new project {}", chosenFile.getAbsolutePath().toString());
	    var serializer = DI.get(IModelSerializer.class);
	    serializer.deserializeProject(chosenFile);
	    PreferenceUtil.lastOpenedProject(chosenFile.getAbsolutePath().toString());
	    DI.get(IRestartableApplication.class).restart();
	} catch (SerializationException | IOException e) {
	    logger.error("Failed opening project: " + e.getMessage());
	}
    }
}

