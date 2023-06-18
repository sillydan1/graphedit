package dk.gtz.graphedit.tool;

import java.util.Optional;

import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.view.events.ViewportKeyEvent;
import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import javafx.scene.Node;

public interface ITool {
    Optional<String> getTooltip();
    Node getGraphic();
    // TODO: String getHelpDescription();
    void onViewportMouseEvent(ViewportMouseEvent e);
    void onVertexMouseEvent(VertexMouseEvent e);
    void onEdgeMouseEvent(EdgeMouseEvent e);
    void onKeyEvent(ViewportKeyEvent e);
}

