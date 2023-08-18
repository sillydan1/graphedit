package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.undo.Undoable;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.yalibs.yadi.DI;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class VertexDragMoveTool extends AbstractBaseTool {
    private final DoubleProperty oldX = new SimpleDoubleProperty();
    private final DoubleProperty oldY = new SimpleDoubleProperty();
    private final DoubleProperty oldPointX = new SimpleDoubleProperty();
    private final DoubleProperty oldPointY = new SimpleDoubleProperty();
    private final ObjectProperty<Runnable> undoAction = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Runnable> redoAction = new SimpleObjectProperty<>(null);
    private final IUndoSystem undoSystem;

    public VertexDragMoveTool() {
        undoSystem = DI.get(IUndoSystem.class);
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
        var point = e.vertex().position();
        if(!e.event().isPrimaryButtonDown())
            return;
        var newX = e.event().getScreenX();
        var newY = e.event().getScreenY();
        var moveDiffX = (newX - oldX.get()) / e.viewportAffine().getMxx();
        var moveDiffY = (newY - oldY.get()) / e.viewportAffine().getMyy();
        point.getXProperty().set(oldPointX.get() + moveDiffX);
        point.getYProperty().set(oldPointY.get() + moveDiffY);
        if(e.editorSettings().gridSnap().get())
            snapToGrid(point, e.editorSettings());

        var xcpy = point.getX();
        var ycpy = point.getY();
        redoAction.set(() -> { 
            point.getXProperty().set(xcpy);
            point.getYProperty().set(ycpy);
        });
    }

    // TODO: Move this to somewhere so it can be used by other tools as well
    private void snapToGrid(ViewModelPoint point, ViewModelEditorSettings settings) {
        point.getXProperty().set(point.getX() - (point.getX() % settings.gridSizeX().get()));
        point.getYProperty().set(point.getY() - (point.getY() % settings.gridSizeY().get()));
    }

    private void handleMouseReleasedEvent(VertexMouseEvent e) {
        if(undoAction.get() != null && redoAction.get() != null)
            undoSystem.push(new Undoable("move action", undoAction.get(), redoAction.get()));
        undoAction.set(null);
        redoAction.set(null);
    }
}

