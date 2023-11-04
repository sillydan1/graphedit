package dk.gtz.graphedit.plugins;

import java.io.IOException;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.spi.IPluginPanel;
import dk.gtz.graphedit.view.ProjectFilesViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ProjectFilesViewPanel implements IPluginPanel {
    private final Node panel;

    public ProjectFilesViewPanel() {
        try {
			panel = new FXMLLoader(ProjectFilesViewController.class.getResource("ProjectFilesView.fxml")).load();
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
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

