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

    /**
     * Construct a new instance
     * @param owner The root stage
     * @param managed Whether if this tracker is externally managed or not
     */
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

    /**
     * Get the X value property
     * @return The X-value property of the mouse
     */
    public DoubleProperty getXProperty() {
        return xProperty;
    }

    /**
     * Get the Y value property
     * @return The Y-value property of the mouse
     */
    public DoubleProperty getYProperty() {
        return yProperty;
    }

    /**
     * Get the uuid of the mousetracker.
     * This is useful when determining if an edge is targeting the mousetracker or a vertex
     * @return The uuid of the mousetracker
     */
    public UUID getTrackerUUID() {
        return uuid;
    }
}
