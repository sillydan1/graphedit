package dk.gtz.graphedit.view;

import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.application.Application;
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
	DI.get(IBufferContainer.class).open(
		UUID.randomUUID().toString(),
		new ViewModelProjectResource(
		    new ModelProjectResource(
			new HashMap<>(),
			new ModelGraph("", new HashMap<>(), new HashMap<>()))));
    }
}

