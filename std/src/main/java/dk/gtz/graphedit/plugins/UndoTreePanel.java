package dk.gtz.graphedit.plugins;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.plugins.view.UndoTreePanelController;
import dk.gtz.graphedit.spi.IPluginPanel;
import javafx.scene.Node;

public class UndoTreePanel implements IPluginPanel {
    private final Node panel;

    public UndoTreePanel() {
	panel = new UndoTreePanelController();
    }

    @Override
    public String getTooltip() {
	return "Undo Tree";
    }

    @Override
    public Node getIcon() {
	return new FontIcon(BootstrapIcons.ARROW_CLOCKWISE);
    }

    @Override
    public Node getPanel() {
	return panel;
    }
}
