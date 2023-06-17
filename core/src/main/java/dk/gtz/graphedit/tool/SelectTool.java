package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.Node;

public class SelectTool extends AbstractVertexDragMoveTool {
    @Override
    public Optional<String> getTooltip() {
        return Optional.of("Select and move syntactic elements");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.CURSOR);
    }
}

