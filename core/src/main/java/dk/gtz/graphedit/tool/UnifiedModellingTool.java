package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.events.EdgeMouseEvent;
import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * An aggregate tool that unifies the following tools:
 *
 * - vertex creation (Shift+click)
 * - vertex moving (Leftmouse Drag)
 * - vertex deletion (Delete or Backspace key)
 * - edge creation (Shift+click vertex)
 * - edge deletion (Delete or Backspace key)
 * - selection management (Leftmouse Click)
 */
public class UnifiedModellingTool extends AbstractBaseTool {
    private final VertexCreateTool vertexCreateTool;
    private final VertexDragMoveTool vertexDragMoveTool;
    private final VertexDeleteTool vertexDeleteTool;
    private final EdgeCreateTool edgeCreateTool;
    private final EdgeDeleteTool edgeDeleteTool;
    private final SelectTool selectTool;
    private final LintInspectorTool hoverTool;
    private final ClipboardTool clipboardTool;

    /**
     * Cronstruct a new instance
     */
    public UnifiedModellingTool() {
        this.vertexCreateTool = new VertexCreateTool();
        this.vertexDragMoveTool = new VertexDragMoveTool();
        this.vertexDeleteTool = new VertexDeleteTool();
        this.edgeCreateTool = new EdgeCreateTool();
        this.edgeDeleteTool = new EdgeDeleteTool();
        this.selectTool = new SelectTool();
        this.hoverTool = new LintInspectorTool();
        this.clipboardTool = new ClipboardTool();
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
             - vertex deletion (Delete or Backspace key)
             - edge creation (Shift+click vertex)
             - edge deletion (Delete or Backspace key)
             - selection management (Leftmouse Click)
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
        if(!isDeleteKeyCombo(e.event()))
            return;
        for(var element : selectTool.getSelection()) {
            if(element.selectable() instanceof ViewModelEdge edge)
                edgeDeleteTool.delete(element.id(), edge, e.graph());
            if(element.selectable() instanceof ViewModelVertex vertex)
                vertexDeleteTool.delete(element.id(), vertex, e.graph());
        }
    }

    private boolean isDeleteKeyCombo(KeyEvent event) {
        var delete = event.getCode().equals(KeyCode.DELETE);
        // This is good for keyboards without a delete button (e.g. some macbooks)
        var shortcutShiftBackspace = (event. getCode().equals(KeyCode.BACK_SPACE)) && event.isShortcutDown() && event.isShiftDown();
        return delete || shortcutShiftBackspace;
    }
}
