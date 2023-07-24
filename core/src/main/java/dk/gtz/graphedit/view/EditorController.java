package dk.gtz.graphedit.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.view.util.PreferenceUtil;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private void initialize() {
	modalPane = DI.get(ModalPane.class);
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
    private void featureHolder() {
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

