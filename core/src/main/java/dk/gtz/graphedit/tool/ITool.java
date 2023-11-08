package dk.gtz.graphedit.tool;

import java.util.Optional;

import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import dk.gtz.graphedit.events.EdgeMouseEvent;
import javafx.scene.Node;

public interface ITool {
    Optional<String> getTooltip();
    Node getGraphic();
    String getHelpDescription();
    void onViewportMouseEvent(ViewportMouseEvent e);
    void onVertexMouseEvent(VertexMouseEvent e);
    void onEdgeMouseEvent(EdgeMouseEvent e);
    void onKeyEvent(ViewportKeyEvent e);
}

