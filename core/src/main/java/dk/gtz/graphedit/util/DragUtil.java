package dk.gtz.graphedit.util;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * Utility class for mousedragging behaviors.
 * Note that there are differing "types" of the concept of "dragging" with a mouse.
 *  - If the intention is to have an object follow the mouse, this class simply calls that "dragging"
 *  - If the intention is to have a viewport and all its elements move with the mouse, this class calls that "inverse dragging"
 *  - If the intention is to have a drag and drop behavior, this class calls that "dnd"
 */
public class DragUtil {
    private DragUtil() {

    }

    /**
     * Add some event handlers to the provided node that makes it draggable through a {@link ViewModelPoint}
     * @param node The node that will catch the related mouse events
     * @param point View model point values that will be modified
     * @param viewportAffine Affine of the viewport
     * @param buffer The buffer to push undoable actions to
     */
    public static void makeDraggable(Node node, ViewModelPoint point, Affine viewportAffine, ViewModelProjectResource buffer) {
        // NOTE: Must be properties due to java's final/effectively final lambda restriction
        var oldX = new SimpleDoubleProperty();
        var oldY = new SimpleDoubleProperty();
        var oldPointX = new SimpleDoubleProperty();
        var oldPointY = new SimpleDoubleProperty();
        var undoAction = new SimpleObjectProperty<Runnable>(null);
        var redoAction = new SimpleObjectProperty<Runnable>(null);

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if(!event.isPrimaryButtonDown())
                return;
            oldX.set(event.getScreenX());
            oldY.set(event.getScreenY());
            var xcpy = point.getX();
            var ycpy = point.getY();
            oldPointX.set(xcpy);
            oldPointY.set(ycpy);
            undoAction.set(() -> { 
                point.getXProperty().set(xcpy); 
                point.getYProperty().set(ycpy); 
            });
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if(!event.isPrimaryButtonDown())
                return;
            var newX = event.getScreenX();
            var newY = event.getScreenY();
            var moveDiffX = (newX - oldX.get()) / viewportAffine.getMxx();
            var moveDiffY = (newY - oldY.get()) / viewportAffine.getMyy();
            point.getXProperty().set(oldPointX.get() + moveDiffX);
            point.getYProperty().set(oldPointY.get() + moveDiffY);
            var xcpy = point.getX();
            var ycpy = point.getY();
            redoAction.set(() -> { 
                point.getXProperty().set(xcpy);
                point.getYProperty().set(ycpy);
            });
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if(undoAction.get() != null && redoAction.get() != null)
                buffer.getUndoSystem().push(new Undoable("move action", undoAction.get(), redoAction.get()));
        });
    }

    /**
     * Add event handlers to the provided node such that the provided {@link Affine} will be inversely translated on mouse dragging
     * Note that the drag events will not be registered as undoable actions
     *
     * NB: The provided affine only gets changes on mouse secondary button presses
     * @param node The node that will catch the related mouse events
     * @param transform The affine to add translation delta to
     */
    public static void makeDraggableInverse(Node node, Affine transform) {
        // NOTE: Must be properties due to java's final/effectively final lambda restriction
        var drawPaneDragStartX = new SimpleDoubleProperty();
        var drawPaneDragStartY = new SimpleDoubleProperty();

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            drawPaneDragStartX.set(event.getX());
            drawPaneDragStartY.set(event.getY());
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if(!event.isSecondaryButtonDown())
                return;
            var dx = event.getX() - drawPaneDragStartX.get();
            var dy = event.getY() - drawPaneDragStartY.get();
            transform.appendTranslation(dx, dy);
            drawPaneDragStartX.set(event.getX());
            drawPaneDragStartY.set(event.getY());
        });
    }
}
