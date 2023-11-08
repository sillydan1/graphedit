package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginPanel;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.yalibs.yadi.DI;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SidePanelController {
    private static Logger logger = LoggerFactory.getLogger(SidePanelController.class);
    private static float PLUGIN_ICON_SCALE_FACTOR = 1.35f;
    @FXML
    public BorderPane root;
    @FXML
    public VBox left;
    private final ObjectProperty<IPluginPanel> selectedPlugin = new SimpleObjectProperty<>(null);

    public void initialize() {
	var plugins = DI.get(IPluginsContainer.class);
	if(plugins.getPlugins().isEmpty()) {
	    logger.warn("No plugins are loaded, cannot show sidepanel");
	    return;
	}
	left.setSpacing(20);
	left.setPadding(new Insets(15));
	for(var plugin : plugins.getPlugins()) {
	    try {
		initializePluginTab(plugin);
	    } catch(Exception e) {
		logger.error("could not initialize plugin tab for plugin: {}", plugin.getName(), e);
	    }
	}
	selectedPlugin.addListener((e,o,n) -> root.setCenter(n.getPanel()));
    }

    private void initializePluginTab(IPlugin plugin) throws Exception {
	for(var panel : plugin.getPanels()) {
	    var btn = new ToggleButton(null, panel.getIcon());
	    btn.setScaleX(PLUGIN_ICON_SCALE_FACTOR);
	    btn.setScaleY(PLUGIN_ICON_SCALE_FACTOR);
	    btn.setTooltip(new Tooltip(panel.getTooltip()));
	    btn.getStyleClass().addAll(Styles.BUTTON_ICON);
	    btn.setOnMouseClicked(e -> selectedPlugin.set(panel));
	    btn.selectedProperty().set(selectedPlugin.get() == panel);
	    selectedPlugin.addListener((e,o,n) -> btn.selectedProperty().set(n == panel));
	    left.getChildren().add(btn);
	    if(selectedPlugin.get() == null) {
		selectedPlugin.set(panel);
		root.setCenter(panel.getPanel());
	    }
	}
    }
}
