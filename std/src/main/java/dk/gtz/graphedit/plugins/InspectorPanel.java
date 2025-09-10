package dk.gtz.graphedit.plugins;

import java.io.IOException;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.plugins.view.InspectorController;
import dk.gtz.graphedit.spi.IPluginPanel;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.viewmodel.IInspectable;
import dk.gtz.graphedit.viewmodel.IPropertyInspector;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.scene.Node;

public class InspectorPanel implements IPluginPanel, IPropertyInspector {
    private final InspectorController panel;

    public InspectorPanel() throws IOException {
        panel = new InspectorController();
        DI.add(IPropertyInspector.class, this);
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

    @Override
    public void inspect(IInspectable inspectable, String propertyName) {
        EditorActions.focusPluginPanel(this);
        Platform.runLater(() -> {
            panel.inspect(inspectable, propertyName);
        });
    }
}
