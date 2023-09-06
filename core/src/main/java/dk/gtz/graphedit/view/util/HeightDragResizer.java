package dk.gtz.graphedit.view.util;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * javafx utility for adding vertical drag-to-resize functionality to a {@code Region}
 */
public class HeightDragResizer {
    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 5;
    private final Region region;
    private double scalar, startY, startHeight;
    private boolean initMinHeight;
    private boolean dragging;
    private boolean dragFromTop;

    private HeightDragResizer(Region region, boolean inverted) {
        this.region = region;
        this.initMinHeight = false;
        this.scalar = inverted ? -1 : 1;
        this.dragFromTop = inverted;
    }

    private static void makeResizable(Region region, boolean inverted) {
        var resizer = new HeightDragResizer(region, inverted);
        region.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> resizer.mousePressed(e));
        region.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> resizer.mouseDragged(e));
        region.addEventFilter(MouseEvent.MOUSE_MOVED, e -> resizer.mouseOver(e));
        region.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> resizer.mouseReleased(e));
    }

    /**
     * Make the provided region height-resizable with a handle on the bottom of the region
     * @param region the region to add the handle to
     */
    public static void makeResizableDown(Region region) {
        makeResizable(region, false);
    }

    /**
     * Make the provided region height-resizable with a handle on the top of the region
     * @param region the region to add the handle to
     */
    public static void makeResizableUp(Region region) {
        makeResizable(region, true);
    }

    private void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
        startY = 0;
        startHeight = 0;
    }

    private void mouseOver(MouseEvent event) {
        if(isInDraggableZone(event) || dragging)
            region.setCursor(Cursor.V_RESIZE);
        else
            region.setCursor(Cursor.DEFAULT);
    }

    private boolean isInDraggableZone(MouseEvent event) {
        if(dragFromTop)
            return event.getY() < RESIZE_MARGIN;
        return event.getY() < (region.getHeight() - RESIZE_MARGIN);
    }

    private void mouseDragged(MouseEvent event) {
        if(!dragging)
            return;
        var mousey = event.getScreenY();
        var dy = (mousey - startY) * scalar;
        var newHeight = startHeight + dy;
        region.setPrefHeight(newHeight);
    }

    private void mousePressed(MouseEvent event) {
        if(!isInDraggableZone(event))
            return;
        startY = event.getScreenY();
        startHeight = region.getHeight();
        dragging = true;
        // make sure that the minimum Height is set to the current Height once,
        // setting a min Height that is smaller than the current Height will
        // have no effect
        if (!initMinHeight) {
            region.setMinHeight(region.getHeight());
            initMinHeight = true;
        }
    }
}


