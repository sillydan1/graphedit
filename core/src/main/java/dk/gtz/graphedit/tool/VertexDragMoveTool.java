package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Tool to manipulate the position of vertices.
 *
 * When selected, press down on a vertex and move the mouse around to manipulate to position of said vertex.
 * Note that the action completes when vertex is released.
 */
public class VertexDragMoveTool extends AbstractBaseTool {
    private final DoubleProperty oldX = new SimpleDoubleProperty();
    private final DoubleProperty oldY = new SimpleDoubleProperty();
    private final DoubleProperty oldPointX = new SimpleDoubleProperty();
    private final DoubleProperty oldPointY = new SimpleDoubleProperty();
    private final ObjectProperty<Runnable> undoAction = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Runnable> redoAction = new SimpleObjectProperty<>(null);
    private final IBufferContainer buffers;

    /**
     * Construct a new instance
     */
    public VertexDragMoveTool() {
        buffers = DI.get(IBufferContainer.class);
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool to manipulate the position of vertices.

            When selected, press down on a vertex and move the mouse around to manipulate to position of said vertex.
            Note that the action completes when vertex is released.
            """;
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("edit the position of vertices");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.ARROWS_MOVE);
    }

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_PRESSED))
            handleMousePressedEvent(e);
        if(e.event().getEventType().equals(MouseEvent.MOUSE_DRAGGED))
            handleMouseDraggedEvent(e);
        if(e.event().getEventType().equals(MouseEvent.MOUSE_RELEASED))
            handleMouseReleasedEvent(e);
    }

    private void handleMousePressedEvent(VertexMouseEvent e) {
        var point = e.vertex().position();
        if(!e.event().isPrimaryButtonDown())
            return;
        oldX.set(e.event().getScreenX());
        oldY.set(e.event().getScreenY());
        var xcpy = point.getX();
        var ycpy = point.getY();
        oldPointX.set(xcpy);
        oldPointY.set(ycpy);
        undoAction.set(() -> { 
            point.getXProperty().set(xcpy); 
            point.getYProperty().set(ycpy); 
        });
    }

    private void handleMouseDraggedEvent(VertexMouseEvent e) {
        if(undoAction.get() == null)
            return;
        var point = e.vertex().position();
        if(!e.event().isPrimaryButtonDown())
            return;
        var newX = e.event().getScreenX();
        var newY = e.event().getScreenY();
        var moveDiffX = (newX - oldX.get()) / e.viewportAffine().getMxx();
        var moveDiffY = (newY - oldY.get()) / e.viewportAffine().getMyy();
        var newPoint = point.copy();
        newPoint.getXProperty().set(oldPointX.get() + moveDiffX);
        newPoint.getYProperty().set(oldPointY.get() + moveDiffY);
        if(e.editorSettings().gridSnap().get())
            newPoint.snapToGrid(e.editorSettings());

        var pointCpy = point.copy();
        point.setValue(newPoint);
        redoAction.set(() -> { 
            point.setValue(pointCpy);
        });
    }

    private void handleMouseReleasedEvent(VertexMouseEvent e) {
        if(undoAction.get() != null && redoAction.get() != null)
            DI.get(IBufferContainer.class)
                .get(e.bufferId())
                .getUndoSystem()
                .push(new Undoable("move action", undoAction.get(), redoAction.get()));
        undoAction.set(null);
        redoAction.set(null);
    }
}
