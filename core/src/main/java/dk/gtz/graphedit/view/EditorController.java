package dk.gtz.graphedit.view;

import java.io.File;
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
import dk.gtz.graphedit.view.util.PlatformUtils;
import dk.gtz.graphedit.view.util.PreferenceUtil;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import dk.yalibs.yastreamgobbler.StreamGobbler;
import dk.yalibs.yaundo.IUndoSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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
    @FXML
    private MenuItem runTargetMenuItem;
    private Thread runTargetThread;
    private Optional<ViewModelRunTarget> selectedRunTarget;

    @FXML
    private void initialize() {
	modalPane = DI.get(ModalPane.class);
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

    private void runTarget() {
	if(selectedRunTarget.isEmpty()) {
	    logger.warn("No RunTarget selected");
	    return;
	}
	try {
	    var pb = new ProcessBuilder();
	    if(selectedRunTarget.get().runAsShell().get()) {
		var sb = new StringBuilder(selectedRunTarget.get().command().get());
		for(var argument : selectedRunTarget.get().arguments())
		    sb.append(" ").append(argument.getValueSafe());
		pb.command("/bin/sh", "-c", sb.toString());
	    } else {
		pb.command(selectedRunTarget.get().command().get());
		for(var argument : selectedRunTarget.get().arguments())
		    pb.command().add(argument.get());
	    }
	    var env = pb.environment();
	    var project = DI.get(ViewModelProject.class);
	    env.put("PROJECT_NAME", project.name().getValueSafe());
	    env.put("PROJECT_DIR", project.rootDirectory().getValueSafe());
	    for(var e : selectedRunTarget.get().environment().entrySet())
		env.put(e.getKey().get(), e.getValue().get());
	    if(!selectedRunTarget.get().currentWorkingDirectory().get().isEmpty())
		pb.directory(new File(selectedRunTarget.get().currentWorkingDirectory().get()));
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
	    Platform.runLater(() -> runTargetMenuItem.setText("Start Selected RunTarget"));
	}
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
}

