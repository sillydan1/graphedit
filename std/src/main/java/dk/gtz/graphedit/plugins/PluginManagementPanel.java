package dk.gtz.graphedit.plugins;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.plugins.view.PluginManagementPanelController;
import dk.gtz.graphedit.spi.IPluginPanel;
import javafx.scene.Node;

public class PluginManagementPanel implements IPluginPanel {
	@Override
	public String getTooltip() {
		return "Plugins";
	}

	@Override
	public Node getIcon() {
		return new FontIcon(BootstrapIcons.PLUG);
	}

	@Override
	public Node getPanel() {
		return new PluginManagementPanelController();
	}
}
