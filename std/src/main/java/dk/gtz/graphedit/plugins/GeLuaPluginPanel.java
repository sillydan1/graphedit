package dk.gtz.graphedit.plugins;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.plugins.view.GeLuaPluginPanelController;
import dk.gtz.graphedit.spi.IPluginPanel;
import javafx.scene.Node;

public class GeLuaPluginPanel implements IPluginPanel {
	@Override
	public String getTooltip() {
        return "Graphedit Lua Plugin";
	}

	@Override
	public Node getIcon() {
        return new FontIcon(BootstrapIcons.LIGHTNING_FILL);
	}

	@Override
	public Node getPanel() {
        return new GeLuaPluginPanelController();
	}
}
