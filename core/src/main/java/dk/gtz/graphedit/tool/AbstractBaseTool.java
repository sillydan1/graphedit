package dk.gtz.graphedit.tool;

import java.util.Optional;

import dk.gtz.graphedit.events.EdgeMouseEvent;
import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;

/**
 * Most of the default implementations for a simple {@link ITool}
 */
public abstract class AbstractBaseTool implements ITool {
    protected AbstractBaseTool() {

    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.empty();
    }

    @Override
    public void onViewportMouseEvent(ViewportMouseEvent e) {}

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {}

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {}

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {}
}

