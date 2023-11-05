package dk.gtz.graphedit.spi;

import javafx.scene.Node;

public interface IPluginPanel {
    String getTooltip();
    Node getIcon();
    Node getPanel();
}

