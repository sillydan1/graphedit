package dk.gtz.graphedit.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.BuildConfig;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.view.util.PreferenceUtil;
import dk.gtz.graphedit.view.util.StreamGobbler;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class EditorController {
    private final Logger logger = LoggerFactory.getLogger(EditorController.class);
    private boolean useLightTheme = PreferenceUtil.lightTheme();
    private ModalPane modalPane;
    @FXML
    private ProjectFilesViewController filePaneController;
    @FXML
    private VBox menubarTopBox;
    @FXML
    private StackPane root;
    @FXML
    private Menu runTargetsMenu;
    private Optional<ViewModelRunTarget> selectedRunTarget;

    @FXML
    private void initialize() {
	modalPane = DI.get(ModalPane.class);
	selectedRunTarget = Optional.empty();
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
        if (isSystemMenuBarSupported()) {
            menubarTopBox.setVisible(false);
            menubarTopBox.setManaged(false);
        }
    }

    @FXML
    private void toggleTheme() {
	useLightTheme = !useLightTheme;
	if(useLightTheme)
	    Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
	else
	    Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
	PreferenceUtil.lightTheme(useLightTheme);
    }

    @FXML
    private void undo() {
	DI.get(IUndoSystem.class).undo();
    }

    @FXML
    private void redo() {
	DI.get(IUndoSystem.class).redo();
    }

    @FXML
    private void save() {
	var serializer = DI.get(IModelSerializer.class);
	var buffers = DI.get(IBufferContainer.class).getBuffers().entrySet();
	logger.trace("save starting");
	buffers.parallelStream().forEach((buffer) -> {
	    try {
		var filePath = buffer.getKey();
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
	Toast.success("save complete");
    }

    @FXML
    private void loadProject() {

    }

    @FXML
    private void quit() {
	Platform.exit();
    }

    @FXML
    private void featureHolder() throws Exception {
    }

    @FXML
    private void editRunTargets() {
	try {
	    var loader = new FXMLLoader(EditorController.class.getResource("RunTargetsEditor.fxml"));
	    var content = (Pane)loader.load();
	    modalPane.show(content);
	    content.requestFocus();
	} catch(IOException e) {
	    logger.error(e.getMessage(), e);
	}
    }

    @FXML
    private void runSelectedRunTarget() {
        try {
	    // TODO: Consider doing all of this in a separate thread
	    if(selectedRunTarget.isEmpty()) {
		logger.warn("No RunTarget selected");
		return;
	    }
            var pb = new ProcessBuilder(selectedRunTarget.get().command().get());
	    for(var argument : selectedRunTarget.get().arguments())
		pb.command().add(argument.get());
            var env = pb.environment();
            env.put("VAR1", "myValue"); // TODO: Add default environment variables, e.g. ${PROJECT_DIR}
	    for(var e : selectedRunTarget.get().environment().entrySet())
		env.put(e.getKey().get(), e.getValue().get());
            // pb.directory(new File("myDir")); // TODO: Consider adding a "cwd" field to RunTarget class
            pb.redirectErrorStream(true);
            var p = pb.start();
            var outputGobbler = new StreamGobbler(p.getInputStream(), logger::info);
            new Thread(outputGobbler).start();
            p.waitFor();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @FXML
    private void openAboutPane() {
	// TODO: Write a better description
	var aboutNode = new VBox(
		createText(BuildConfig.APP_NAME, Styles.TITLE_1),
		createText(BuildConfig.APP_VERSION, Styles.TITLE_3),
		new Text("TODO: longer description"));
	aboutNode.setMinSize(450, 450);
	aboutNode.setMaxSize(450, 450);
	aboutNode.getStyleClass().add(Styles.BG_DEFAULT);
	modalPane.show(aboutNode);
    }

    private Text createText(String text, String style) {
	var returnValue = new Text(text);
	returnValue.getStyleClass().add(style);
	return returnValue;
    }

    @FXML
    private void openSearchPane() {
	try {
	    var loader = new FXMLLoader(EditorController.class.getResource("SearchPane.fxml"));
	    var content = (Pane)loader.load();
	    var controller = (SearchPaneController)loader.getController();
	    controller.onClose(modalPane::hide);
	    modalPane.show(content);
	    content.requestFocus();
	    controller.focus();
	} catch(IOException e) {
	    logger.error(e.getMessage(), e);
	}
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

    // TODO: Move into general utilities library
    private boolean isSystemMenuBarSupported() {
	var os = System.getProperty("os.name").toLowerCase();
	var platform = System.getProperty("javafx.platform");
	if(os.contains("win"))
	    return true;
	if(os.contains("mac"))
	    return true;
	if(os.contains("nix") || os.contains("nux"))
	    if(platform != null && platform.equals("gtk"))
		return true;
	return false;
    }
}

