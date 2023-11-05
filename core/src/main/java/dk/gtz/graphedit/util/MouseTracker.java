package dk.gtz.graphedit.util;

import java.util.UUID;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * A mouse tracking utility
 */
public class MouseTracker {
    private final DoubleProperty xProperty;
    private final DoubleProperty yProperty;
    private final UUID uuid;

    public MouseTracker(Stage owner, boolean managed) {
        uuid = UUID.randomUUID();
        xProperty = new SimpleDoubleProperty();
        yProperty = new SimpleDoubleProperty();
        if(managed)
            return;
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

    public UUID getTrackerUUID() {
        return uuid;
    }
}

