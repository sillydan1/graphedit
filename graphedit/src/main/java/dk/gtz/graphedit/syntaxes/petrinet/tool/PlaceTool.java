package dk.gtz.graphedit.syntaxes.petrinet.tool;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.tool.AbstractBaseTool;
import javafx.scene.Node;

public class PlaceTool extends AbstractBaseTool {
    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.CIRCLE);
    }

    @Override
    public String getHelpDescription() {
        return """
            Create new petrinet place vertices
            """;
    }
}

