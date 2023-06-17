package dk.gtz.graphedit.tool;

import java.util.Optional;

import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public interface ITool {
    Optional<String> getTooltip();
    Node getGraphic();
    // events
    void onViewportMouseEvent(MouseEvent e);
    void onVertexMouseEvent(VertexMouseEvent e);
    void onEdgeMouseEvent(EdgeMouseEvent e);
}

