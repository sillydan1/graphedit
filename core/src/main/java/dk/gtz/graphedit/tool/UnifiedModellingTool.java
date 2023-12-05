package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.events.EdgeMouseEvent;
import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import javafx.scene.Node;

/**
 * An aggregate tool that unifies the following tools:
 *
 * - vertex creation (Shift+click)
 * - vertex moving (Leftmouse Drag)
 * - edge creation (Shift+click vertex)
 * - selection management (Leftmouse Click (hold Ctrl to select more))
 * - element deletion (Delete or Backspace key to delete selection)
 */
public class UnifiedModellingTool extends AbstractBaseTool {
    private final VertexCreateTool vertexCreateTool;
    private final VertexDragMoveTool vertexDragMoveTool;
    private final EdgeCreateTool edgeCreateTool;
    private final SelectTool selectTool;
    private final LintInspectorTool hoverTool;
    private final ClipboardTool clipboardTool;
    private final MassDeleteTool deleteTool;

    /**
     * Cronstruct a new instance
     */
    public UnifiedModellingTool() {
        this.vertexCreateTool = new VertexCreateTool();
        this.vertexDragMoveTool = new VertexDragMoveTool();
        this.edgeCreateTool = new EdgeCreateTool();
        this.selectTool = new SelectTool();
        this.hoverTool = new LintInspectorTool();
        this.clipboardTool = new ClipboardTool();
        this.deleteTool = new MassDeleteTool();
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("unified edit tool");
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool that unifies the following tools:

             - vertex creation (Shift+click)
             - vertex moving (Leftmouse Drag)
             - edge creation (Shift+click vertex)
             - selection management (Leftmouse Click (hold Ctrl to select more))
             - element deletion (Delete or Backspace key to delete selection)
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
        hoverTool.onVertexMouseEvent(e);
        if(edgeCreateTool.isCurrentlyCreatingEdge()) {
            edgeCreateTool.onVertexMouseEvent(e);
            return;
        }
        if(e.event().isShiftDown()) {
            edgeCreateTool.onVertexMouseEvent(e);
            return;
        }
        selectTool.onVertexMouseEvent(e);
        vertexDragMoveTool.onVertexMouseEvent(e);
    }

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {
        hoverTool.onEdgeMouseEvent(e);
        edgeCreateTool.onEdgeMouseEvent(e);
        selectTool.onEdgeMouseEvent(e);
    }

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {
        edgeCreateTool.onKeyEvent(e);
        clipboardTool.onKeyEvent(e);
        deleteTool.onKeyEvent(e);
    }
}
