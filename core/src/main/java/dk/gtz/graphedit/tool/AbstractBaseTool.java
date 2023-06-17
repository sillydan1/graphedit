package dk.gtz.graphedit.tool;

import java.util.Optional;

import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import javafx.scene.input.MouseEvent;

public abstract class AbstractBaseTool implements ITool {
    @Override
    public Optional<String> getTooltip() {
        return Optional.empty();
    }

    @Override
    public void onViewportMouseEvent(MouseEvent e) {}

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {}

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {}
}

