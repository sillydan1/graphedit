package dk.gtz.graphedit.plugins;


import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.spi.IPluginPanel;
import dk.gtz.graphedit.view.ProjectFilesViewController;
import javafx.scene.Node;

public class ProjectFilesViewPanel implements IPluginPanel {
    private final Node panel;

    public ProjectFilesViewPanel() {
        panel = new ProjectFilesViewController();
    }

    @Override
    public Node getIcon() {
        return new FontIcon(BootstrapIcons.FILES);
    }

    @Override
    public Node getPanel() {
        return panel;
    }
}

