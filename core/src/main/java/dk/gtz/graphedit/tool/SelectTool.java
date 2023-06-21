package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import javafx.scene.Node;

public class SelectTool extends AbstractBaseTool {
    private final Logger logger = LoggerFactory.getLogger(SelectTool.class);

    public SelectTool() {

    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("select syntactic elements");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.CURSOR);
    }

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        logger.debug("selected vertex %s".formatted(e.vertexId()));
    }

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {
        logger.debug("selected edge %s".formatted(e.edgeId()));
    }
}

