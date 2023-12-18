package dk.gtz.graphedit.plugins.view;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.Tile;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.view.IRestartableApplication;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow.AnchorLocation;

public class PluginManagementPanelController extends VBox {
    private final VBox container;
    private final ScrollPane scrollPane;
    private final ViewModelEditorSettings settings;
    private final IPluginsContainer plugins;

    public PluginManagementPanelController() {
        settings = DI.get(ViewModelEditorSettings.class);
        plugins = DI.get(IPluginsContainer.class);
	container = new VBox();
	container.setSpacing(10);
	container.setPadding(new Insets(10));
	scrollPane = new ScrollPane(container);
	scrollPane.setFitToWidth(true);
	getChildren().add(getToolbar());
	getChildren().add(scrollPane);
	initializePluginsList();
    }

    private Node getToolbar() {
	var saveButton = new Button(null, new FontIcon(BootstrapIcons.HDD));
	saveButton.getStyleClass().addAll(Styles.BUTTON_ICON);
	var saveTip = new Tooltip("Save Settings");
	saveTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
	saveTip.setPrefWidth(200);
	saveTip.setWrapText(true);
	saveButton.setTooltip(saveTip);
	saveButton.setOnAction(e -> EditorActions.saveEditorSettings(settings));

	var reloadButton = new Button(null, new FontIcon(BootstrapIcons.POWER));
	reloadButton.getStyleClass().addAll(Styles.BUTTON_ICON);
	var reloadTip = new Tooltip("Reload Editor");
	reloadTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
	reloadTip.setPrefWidth(200);
	reloadTip.setWrapText(true);
	reloadButton.setTooltip(reloadTip);
	reloadButton.setOnAction(e -> DI.get(IRestartableApplication.class).restart());
	return new ToolBar(saveButton, reloadButton);
    }

    private void initializePluginsList() {
	for(var plugin : plugins.getPlugins())
	    container.getChildren().add(getPluginEntry(plugin));
    }

    private Node getPluginEntry(IPlugin plugin) {
	var result = new Tile(plugin.getName(), "");
	var enabledThingy = new ToggleSwitch();
	enabledThingy.setSelected(!settings.disabledPlugins().contains(plugin.getName()));
	enabledThingy.selectedProperty().addListener((e,o,n) -> {
	    if(n)
		settings.disabledPlugins().remove(plugin.getName());
	    else
		settings.disabledPlugins().add(plugin.getName());
	});
	result.setAction(enabledThingy);
	result.setActionHandler(enabledThingy::fire);
	var description = plugin.getDescription();
	if(!description.isBlank())
	    result.setTooltip(new Tooltip(description));
	return result;
    }
}
