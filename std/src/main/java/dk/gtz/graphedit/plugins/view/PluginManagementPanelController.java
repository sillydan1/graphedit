package dk.gtz.graphedit.plugins.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import atlantafx.base.controls.Tile;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.gtz.graphedit.util.Download;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.view.IRestartableApplication;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow.AnchorLocation;

public class PluginManagementPanelController extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(PluginManagementPanelController.class);
    private final VBox installedContainer;
    private final VBox availableContainer;
    private final ScrollPane scrollPane;
    private final ViewModelEditorSettings settings;
    private final IPluginsContainer plugins;
    private static record PluginDbEntry(String name, String version, String downloadUrl) {}
    public static record PluginDb(Map<String,Map<String,String>> db) {}

    public PluginManagementPanelController() {
        settings = DI.get(ViewModelEditorSettings.class);
        plugins = DI.get(IPluginsContainer.class);
	installedContainer = new VBox();
	availableContainer = new VBox();
	var installed = new TitledPane("Installed", installedContainer);
	var available = new TitledPane("Available", availableContainer);
	scrollPane = new ScrollPane(new Accordion(installed, available));
	scrollPane.setFitToWidth(true);
	getChildren().add(getToolbar());
	getChildren().add(scrollPane);
	initializeInstalledPluginsList();
	initializeAvailablePluginsList();
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

    private void initializeInstalledPluginsList() {
	for(var plugin : plugins.getPlugins())
	    installedContainer.getChildren().add(getPluginEntry(plugin));
    }

    private void initializeAvailablePluginsList() {
	for(var plugin : getPluginDb())
	     availableContainer.getChildren().add(getInstallablePluginEntry(plugin));
    }

    private List<PluginDbEntry> getPluginDb() {
	// TODO: This list should be customizable and saved somewhere (not the ViewModelEditorSettings)
	var pluginDbs = List.of(
		"https://raw.githubusercontent.com/sillydan1/graphedit-plugindb/main/plugindb.json"
		);
	var mapper = new JsonMapper();
	mapper.registerModule(new Jdk8Module());
	for(var db : pluginDbs) {
	    logger.trace("loading plugindb: {}", db);
	    try(var input = new BufferedInputStream(new URL(db).openStream())) {
		// NOTE: If your plugin needs to download other external files 
		//       do it as part of the plugin startup sequence (check for dependencies, if they dont exist
		//       prompt the user for download or notify about missing dependencies etc.).
		var file = mapper.readValue(input.readAllBytes(), PluginDb.class);
		var result = new ArrayList<PluginDbEntry>();
		for(var entry : file.db().entrySet())
		    for(var version : entry.getValue().entrySet())
			result.add(new PluginDbEntry(entry.getKey(), version.getKey(), version.getValue()));
		return result;
	    } catch(IOException e) {
		logger.error("processing '{}' failed: {}", db, e.getMessage());
	    }
	}
	return List.of();
    }

    private Node getPluginEntry(IPlugin plugin) {
	// TODO: IPlugin should have a 'getVersion' thing
	var result = new Tile(plugin.getName(), "");
	var enableSwitch = new ToggleSwitch();
	enableSwitch.setSelected(!settings.disabledPlugins().contains(plugin.getName()));
	enableSwitch.selectedProperty().addListener((e,o,n) -> {
	    if(n)
		settings.disabledPlugins().remove(plugin.getName());
	    else
		settings.disabledPlugins().add(plugin.getName());
	});
	result.setAction(enableSwitch);
	result.setActionHandler(enableSwitch::fire);
	result.setTooltip(new Tooltip(plugin.getDescription()));
	return result;
    }

    private Node getInstallablePluginEntry(PluginDbEntry entry) {
	// TODO: description should also contain url and a "go to url" button or something
	var result = new Tile(entry.name(), entry.version());
	var downloadButton = new Button("Install");
	downloadButton.setOnAction(e -> downloadPlugin(downloadButton, entry));
	result.setAction(downloadButton);
	result.setActionHandler(downloadButton::fire);
	result.setTooltip(new Tooltip(entry.downloadUrl()));
	return result;
    }

    private void downloadPlugin(Button button, PluginDbEntry entry) {
	try {
	    var originalButtonText = button.getText();
	    var url = new URL(entry.downloadUrl());
	    var d = new Download(url);
	    d.setOnStateChanged(() -> {
		try {
		    switch (d.getStatus()) {
			case DOWNLOADING:
			    button.setText("%.f%".formatted(d.getProgress()));
			    break;
			case ERROR:
			    button.setText("download error");
			    break;
			case PAUSED:
			    button.setText("download paused");
			    break;
			case COMPLETE:
			    var filepath = String.join(File.separator, EditorActions.getConfigDir(), "plugins", d.getFileName(url));
			    Files.copy(Path.of(d.downloadedFile().get()), Path.of(filepath), StandardCopyOption.REPLACE_EXISTING);
			case CANCELLED:
			default:
			    button.setText(originalButtonText);
			    break;
		    }
		} catch (IOException e) {
		    logger.error("failed to download plugin: {}", e.getMessage(), e);
		}
	    });
	    d.download();
	} catch(IOException e) {
	    logger.error("failed to download plugin: {}", e.getMessage(), e);
	}
    }
}
