package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.undo.Undoable;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.yalibs.yadi.DI;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public class DragUtil {
    private static Logger logger = LoggerFactory.getLogger(DragUtil.class);

    public static void makeDraggable(Node mouseSubject, ViewModelPoint point, Affine viewportAffine) {
        // Must be properties due to java's final/effectively final lambda restriction
        var undoSystem = DI.get(IUndoSystem.class);
        var oldX = new SimpleDoubleProperty();
        var oldY = new SimpleDoubleProperty();
        var oldPointX = new SimpleDoubleProperty();
        var oldPointY = new SimpleDoubleProperty();
        var undoAction = new SimpleObjectProperty<Runnable>(null);
        var redoAction = new SimpleObjectProperty<Runnable>(null);

        mouseSubject.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
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

        mouseSubject.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
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

        mouseSubject.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if(undoAction.get() != null && redoAction.get() != null)
                undoSystem.push(new Undoable("move action", undoAction.get(), redoAction.get()));
        });
    }
}

