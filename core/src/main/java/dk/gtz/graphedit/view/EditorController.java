package dk.gtz.graphedit.view;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.util.HeightDragResizer;
import dk.gtz.graphedit.util.PlatformUtils;
import dk.gtz.graphedit.util.WidthDragResizer;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * View controller for the main editor.
 */
public class EditorController {
    private final Logger logger = LoggerFactory.getLogger(EditorController.class);
    @FXML
    private VBox menubarTopBox;
    @FXML
    private StackPane root;
    @FXML
    private BorderPane primaryBorderPane;
    @FXML
    private BorderPane bottomBorderPane;
    @FXML
    private Menu runTargetsMenu;
    @FXML
    private Menu importFileMenu;
    @FXML
    private Menu exportFileMenu;
    @FXML
    private MenuItem runTargetMenuItem;
    @FXML
    private SidePanelController sidePanelController;
    private Thread runTargetThread;
    private Optional<ViewModelRunTarget> selectedRunTarget;

    /**
     * Constructs a new instance of the editor view controller
     */
    public EditorController() {

    }

    @FXML
    private void initialize() {
	selectedRunTarget = Optional.empty();
	runTargetThread = new Thread(this::runTarget);
	WidthDragResizer.makeResizableRight((Region)primaryBorderPane.getLeft());
	((Region)primaryBorderPane.getLeft()).setPrefWidth(400);
	HeightDragResizer.makeResizableUp((Region)primaryBorderPane.getBottom());
	updateImporters();
	updateExporters();
	initProjectMenu();
	hideTopbarOnSupportedPlatforms();
	bottomBorderPane.setBottom(new StatusBarController());
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

    private void updateImporters() {
	importFileMenu.getItems().clear();
	var plugins = DI.get(IPluginsContainer.class);
	for(var plugin : plugins.getEnabledPlugins()) {
	    for(var importer : plugin.getImporters()) {
		var fileImporter = new MenuItem(importer.getName());
		fileImporter.setOnAction(e -> {
		    try {
			var filetypes = importer.getFiletypesFilter();
			var files = EditorActions.openFiles(filetypes.description(), filetypes.extensions());
			if(files.isEmpty())
			    return;
			var result = importer.importFiles(files);
			for(var importResult : result) {
			    EditorActions.saveModelToFile(importResult.newFileLocation(), importResult.newModel());
			    EditorActions.openModel(importResult.newFileLocation());
			}
		    } catch(IOException exc) {
			throw new RuntimeException(exc);
		    }
		});
		importFileMenu.getItems().add(fileImporter);
	    }
	}
    }

    private void updateExporters() {
	exportFileMenu.getItems().clear();
	var plugins = DI.get(IPluginsContainer.class);
	for(var plugin : plugins.getEnabledPlugins()) {
	    for(var exporter : plugin.getExporters()) {
		var fileExporter = new MenuItem(exporter.getName());
		fileExporter.setOnAction(e -> {
		    var projectDir = Path.of(DI.get(ViewModelProject.class).rootDirectory().getValue());
		    EditorActions.exportFiles(exporter, List.of(projectDir.toFile().listFiles()));
		});
		exportFileMenu.getItems().add(fileExporter);
	    }
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
    private void newModel() {
	EditorActions.createNewModelFile();
    }

    @FXML
    private void openModel() {
	EditorActions.openModel();
    }

    @FXML
    private void newProject() {
	var file = EditorActions.newFile();
	if(!file.isPresent())
	    return;
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
