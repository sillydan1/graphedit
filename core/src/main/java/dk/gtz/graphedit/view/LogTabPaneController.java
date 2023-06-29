package dk.gtz.graphedit.view;


import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import ch.qos.logback.classic.Level;
import dk.gtz.graphedit.logging.EditorLogAppender;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class LogTabPaneController {
    private static Logger logger = LoggerFactory.getLogger(LogTabPaneController.class);
    @FXML
    private TabPane tabpane;

    public LogTabPaneController() {

    }

    @FXML
    private void initialize() {
	tabpane.getTabs().add(createLogTab(Level.INFO, BootstrapIcons.INFO_CIRCLE, Styles.ACCENT));
	tabpane.getTabs().add(createLogTab(Level.WARN, BootstrapIcons.EXCLAMATION_TRIANGLE, Styles.WARNING));
	tabpane.getTabs().add(createLogTab(Level.ERROR, BootstrapIcons.EXCLAMATION_CIRCLE, Styles.DANGER));
    }

    private Tab createLogTab(Level level, BootstrapIcons icon, String style) {
	var tab = new Tab(level.levelStr);
	var fontIcon = new FontIcon(icon);
	tab.setGraphic(fontIcon);
	tab.getGraphic().getStyleClass().add(style);
	var log = new LogTabController();
	tab.setContent(log);
	EditorLogAppender.subscribe(level, log::onLogAdded);
	tab.setClosable(false);
	return tab;
    }
}

