package dk.gtz.graphedit.view;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public class DragUtil {
    public static void makeDraggable(Node mouseSubject, ViewModelPoint point, Affine viewportAffine) {
        // Must be properties due to java's final/effectively final lambda restriction
        var oldX = new SimpleDoubleProperty();
        var oldY = new SimpleDoubleProperty();
        var oldPointX = new SimpleDoubleProperty();
        var oldPointY = new SimpleDoubleProperty();

        mouseSubject.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if(!event.isPrimaryButtonDown())
                return;
            oldX.set(event.getScreenX());
            oldY.set(event.getScreenY());
            oldPointX.set(point.getX());
            oldPointY.set(point.getY());
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
        });

        mouseSubject.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
        });
    }
}

