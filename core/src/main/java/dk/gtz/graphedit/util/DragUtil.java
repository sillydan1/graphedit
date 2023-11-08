package dk.gtz.graphedit.util;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public class DragUtil {
    /**
     * Add some event handlers to the provided node that makes it draggable through a {@link ViewModelPoint}
     * @param node The node that will catch the related mouse events
     * @param point View model point values that will be modified
     * @param viewportAffine Affine of the viewport
     */
    public static void makeDraggable(Node node, ViewModelPoint point, Affine viewportAffine) {
        // NOTE: Must be properties due to java's final/effectively final lambda restriction
        var undoSystem = DI.get(IUndoSystem.class);
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
                undoSystem.push(new Undoable("move action", undoAction.get(), redoAction.get()));
        });
    }

    /**
     * Add event handlers to the provided node such that the provided {@link Affine} will be inversely translated on mouse dragging
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
