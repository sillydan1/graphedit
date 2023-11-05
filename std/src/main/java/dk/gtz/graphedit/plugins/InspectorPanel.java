package dk.gtz.graphedit.plugins;

import java.io.IOException;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.plugins.view.InspectorController;
import dk.gtz.graphedit.spi.IPluginPanel;
import javafx.scene.Node;

public class InspectorPanel implements IPluginPanel {
    private final Node panel;

    public InspectorPanel() throws IOException {
        panel = new InspectorController();
    }

    @Override
    public String getTooltip() {
        return "Attribute Inspector";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(BootstrapIcons.CARD_TEXT);
    }

    @Override
    public Node getPanel() {
        return panel;
    }

}

