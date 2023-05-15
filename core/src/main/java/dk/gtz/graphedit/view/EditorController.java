package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import dk.gtz.graphedit.skyhook.DI;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

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
    private void addPlaceholderTab() {
	var controller = DI.get(EditorTabPaneController.class);
	controller.tabpane.getTabs().add(0, new DraggableTab("Placeholder"));
    }
}

