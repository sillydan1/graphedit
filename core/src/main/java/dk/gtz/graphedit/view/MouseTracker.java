package dk.gtz.graphedit.view;

import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import javafx.beans.property.*;

/**
 * A mouse tracking utility class, access through the skyhook DI system
 */
public class MouseTracker {
    private final DoubleProperty xProperty;
    private final DoubleProperty yProperty;

    public MouseTracker(Stage owner) {
        xProperty = new SimpleDoubleProperty();
        yProperty = new SimpleDoubleProperty();
        owner.addEventFilter(MouseEvent.ANY, event -> {
            if(!Double.isNaN(event.getX()))
                xProperty.set(event.getX());
            if(!Double.isNaN(event.getY()))
                yProperty.set(event.getY());
        });
    }

    public DoubleProperty getXProperty() {
        return xProperty;
    }

    public DoubleProperty getYProperty() {
        return yProperty;
    }
}

