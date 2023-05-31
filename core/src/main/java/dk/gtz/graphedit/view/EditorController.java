package dk.gtz.graphedit.view;

import java.awt.Point;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class EditorController {
    private final Logger logger = LoggerFactory.getLogger(EditorController.class);
    private boolean useLightTheme = false;

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
	var exampleEdges = new HashMap<UUID,ModelEdge>();
	exampleVertices.put(UUID.randomUUID(), new ModelVertex(new Point(50, 0)));
	exampleVertices.put(UUID.randomUUID(), new ModelVertex(new Point(100, 100)));
	exampleVertices.put(UUID.randomUUID(), new ModelVertex(new Point(200, 120)));
	
	DI.get(IBufferContainer.class).open(
		UUID.randomUUID().toString(),
		new ViewModelProjectResource(
		    new ModelProjectResource(
			new HashMap<>(),
			new ModelGraph("", exampleVertices, exampleEdges))));
    }

    @FXML
    private void toastTest() throws Exception {
	Toast.show("Hello World!");
    }

    @FXML
    private void quit() {
	Platform.exit();
    }
}

