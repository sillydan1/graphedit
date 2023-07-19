package dk.gtz.graphedit.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.view.util.PreferenceUtil;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class EditorController {
    private final Logger logger = LoggerFactory.getLogger(EditorController.class);
    private boolean useLightTheme = PreferenceUtil.lightTheme();

    @FXML
    private ProjectFilesViewController filePaneController;

    @FXML
    private VBox menubarTopBox;

    @FXML
    private void initialize() {
	hideTopbarOnSupportedPlatforms();
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
    private void addPlaceholderTab() throws Exception {
	var exampleVertices = new HashMap<UUID,ModelVertex>();
	var exampleEdges = new HashMap<UUID,ModelEdge>();
	var filePath = "/tmp/graphedit/%s.json".formatted(UUID.randomUUID().toString());
	DI.get(IBufferContainer.class).open(
		filePath,
		new ViewModelProjectResource(
		    new ModelProjectResource(
			new HashMap<>(),
			new ModelGraph("", exampleVertices, exampleEdges))));
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

    private int toastTestCounter = 0;
    @FXML
    private void toastTest() throws Exception {
	if(toastTestCounter == 0)
	    logger.info("Hello World! [Lars](not valid)");
	if(toastTestCounter == 1)
	    Toast.success("Hello World!");
	if(toastTestCounter == 2)
	    logger.warn("Hello World! [Lars]("+UUID.randomUUID().toString()+") dhwjka");
	if(toastTestCounter == 3)
	    logger.error("Hello World!");
	toastTestCounter = (toastTestCounter + 1) % 4;
    }

    @FXML
    private void quit() {
	Platform.exit();
    }

    @FXML
    private void featureHolder() {
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

