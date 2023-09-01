package dk.gtz.graphedit.tool;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.view.events.ViewportKeyEvent;
import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class UnifiedModellingTool extends AbstractBaseTool {
    private final VertexCreateTool vertexCreateTool;
    private final VertexDragMoveTool vertexDragMoveTool;
    private final VertexDeleteTool vertexDeleteTool;
    private final EdgeCreateTool edgeCreateTool;
    private final EdgeDeleteTool edgeDeleteTool;
    private final SelectTool selectTool;

    public UnifiedModellingTool() {
        this.vertexCreateTool = new VertexCreateTool();
        this.vertexDragMoveTool = new VertexDragMoveTool();
        this.vertexDeleteTool = new VertexDeleteTool();
        this.edgeCreateTool = new EdgeCreateTool();
        this.edgeDeleteTool = new EdgeDeleteTool();
        this.selectTool = new SelectTool();
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool that unifies the following tools:

            TODO: add more help message
            """;
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.SHARE);
    }

    @Override
    public void onViewportMouseEvent(ViewportMouseEvent e) {
        edgeCreateTool.onViewportMouseEvent(e);
        if(!e.isTargetDrawPane())
            return;
        if(e.event().isShiftDown()) {
            vertexCreateTool.onViewportMouseEvent(e);
            return;
        }
        vertexDragMoveTool.onViewportMouseEvent(e);
        selectTool.onViewportMouseEvent(e);
    }

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        if(edgeCreateTool.isCurrentlyCreatingEdge()) {
            edgeCreateTool.onVertexMouseEvent(e);
            return;
        }
        if(e.event().isShiftDown()) {
            edgeCreateTool.onVertexMouseEvent(e);
            return;
        }
        if(e.event().getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            selectTool.onVertexMouseEvent(e);
            return;
        }
        vertexDragMoveTool.onVertexMouseEvent(e);
    }

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {
        edgeCreateTool.onEdgeMouseEvent(e);
        selectTool.onEdgeMouseEvent(e);
    }

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {
        if(edgeCreateTool.isCurrentlyCreatingEdge())
            edgeCreateTool.onKeyEvent(e);
        if(e.event().getCode().equals(KeyCode.DELETE) || e.event().getCode().equals(KeyCode.BACK_SPACE)) {
            for(var element : selectTool.getSelection()) {
                if(element.selectable() instanceof ViewModelEdge edge)
                    edgeDeleteTool.delete(element.id(), edge, e.graph());
                if(element.selectable() instanceof ViewModelVertex vertex)
                    vertexDeleteTool.delete(element.id(), vertex, e.graph());
            }
        }
    }
}

