package dk.gtz.graphedit.view;


import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import ch.qos.logback.classic.Level;
import dk.gtz.graphedit.logging.EditorLogAppender;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

/**
 * View controll for the log tabpane containing all the logs
 */
public class LogTabPaneController {
    private static Logger logger = LoggerFactory.getLogger(LogTabPaneController.class);
    @FXML
    private TabPane tabpane;

    /**
     * Create a new instance
     */
    public LogTabPaneController() {

    }

    @FXML
    private void initialize() {
	tabpane.getTabs().add(createLogTab(Level.INFO, BootstrapIcons.INFO_CIRCLE, Styles.ACCENT));
	tabpane.getTabs().add(createLogTab(Level.WARN, BootstrapIcons.EXCLAMATION_TRIANGLE, Styles.WARNING));
	tabpane.getTabs().add(createLogTab(Level.ERROR, BootstrapIcons.EXCLAMATION_CIRCLE, Styles.DANGER));
	tabpane.getTabs().add(createLogTab(Level.TRACE, BootstrapIcons.ARCHIVE, ""));
    }

    private Tab createLogTab(Level level, BootstrapIcons icon, String highlightStyle) {
	var tab = new Tab();
	var fontIcon = new FontIcon(icon);
	var tabLabel = new Label(level.levelStr);
	var tabTitleBox = new HBox(fontIcon, tabLabel);
	tabTitleBox.setSpacing(5);
	tab.setGraphic(tabTitleBox);
	var log = new LogTabController();
	tabpane.selectionModelProperty().get().selectedItemProperty().addListener((e,o,n) -> {
	    if(n == tab)
		fontIcon.getStyleClass().removeAll(highlightStyle);
	});
	tab.setContent(log);
	EditorLogAppender.subscribe(level, s -> {
	    if(tabpane.selectionModelProperty().get().getSelectedItem() != tab)
		fontIcon.getStyleClass().add(highlightStyle);
	});
	EditorLogAppender.subscribe(level, log::addLog);
	tab.setClosable(false);
	return tab;
    }
}
