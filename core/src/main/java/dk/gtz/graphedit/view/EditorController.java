package dk.gtz.graphedit.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class EditorController {
    private final Logger logger = LoggerFactory.getLogger(EditorController.class);
    private boolean useLightTheme = false;

    @FXML
    private ProjectFilesViewController filePaneController;

    @FXML
    private void initialize() {

    }

    @FXML
    private void toggleTheme() {
	useLightTheme = !useLightTheme;
	if(useLightTheme)
	    Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
	else
	    Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
    }

    @FXML
    private void addPlaceholderTab() throws Exception {
	var exampleVertices = new HashMap<UUID,ModelVertex>();
	var vert1 = UUID.randomUUID();
	exampleVertices.put(vert1, new ModelVertex(new ModelPoint(0, 0)));
	var vert2 = UUID.randomUUID();
	exampleVertices.put(vert2, new ModelVertex(new ModelPoint(-343, -550)));
	var vert3 = UUID.randomUUID();
	exampleVertices.put(vert3, new ModelVertex(new ModelPoint(343, 550)));
	var vert4 = UUID.randomUUID();
	exampleVertices.put(vert4, new ModelVertex(new ModelPoint(500, 50)));

	var exampleEdges = new HashMap<UUID,ModelEdge>();
	exampleEdges.put(UUID.randomUUID(), new ModelEdge(vert1, vert2));
	exampleEdges.put(UUID.randomUUID(), new ModelEdge(vert2, vert3));
	exampleEdges.put(UUID.randomUUID(), new ModelEdge(vert3, vert4));

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
	buffers.parallelStream().forEach((buffer) -> {
	    try {
		var filePath = buffer.getKey();
		logger.trace("saving file {}", filePath);
		var model = buffer.getValue().toModel();
		var serializedModel = serializer.serialize(model);
		var p = Paths.get(filePath);
		Files.createDirectories(p.getParent());
		Files.write(p, serializedModel.getBytes());
		logger.trace("save complete");
	    } catch (SerializationException e) {
		logger.error("failed to serialize model '{}' reason: {}", buffer.getKey(), e.getMessage());
	    } catch (IOException e) {
		logger.error("failed to save file '{}' reason: {}", buffer.getKey(), e.getMessage());
	    }
	});
    }

    @FXML
    private void loadProject() {

    }

    @FXML
    private void toastTest() throws Exception {
	Toast.show("Hello World!");
    }

    @FXML
    private void quit() {
	Platform.exit();
    }

    @FXML
    private void featureHolder() {
	filePaneController.toggle();
    }
}

