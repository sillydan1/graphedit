package dk.gtz.graphedit.plugins;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.plugins.view.LintPanelController;
import dk.gtz.graphedit.spi.IPluginPanel;
import javafx.scene.Node;

public class LintPanel implements IPluginPanel {

	@Override
	public String getTooltip() {
        return "Lints";
	}

	@Override
	public Node getIcon() {
        return new FontIcon(BootstrapIcons.STARS);
	}

	@Override
	public Node getPanel() {
        return new LintPanelController();
	}
}
