package dk.gtz.graphedit.view.util;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * javafx utility for adding horizontal drag-to-resize functionality to a {@code Region}
 */
public class WidthDragResizer {
    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 5;
    private final Region region;
    private double scalar, startX, startWidth;
    private boolean initMinWidth;
    private boolean dragging;
    private boolean dragFromLeft;

    private WidthDragResizer(Region region, boolean inverted) {
        this.region = region;
        this.initMinWidth = false;
        this.scalar = inverted ? -1 : 1;
        this.dragFromLeft = inverted;
    }

    private static void makeResizable(Region region, boolean inverted) {
        var resizer = new WidthDragResizer(region, inverted);
        region.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> resizer.mousePressed(e));
        region.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> resizer.mouseDragged(e));
        region.addEventFilter(MouseEvent.MOUSE_MOVED, e -> resizer.mouseOver(e));
        region.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> resizer.mouseReleased(e));
    }

    /**
     * Make the provided region width-resizable with a handle on the right side of the region
     * @param region the region to add the handle to
     */
    public static void makeResizableRight(Region region) {
        makeResizable(region, false);
    }

    /**
     * Make the provided region width-resizable with a handle on the left side of the region
     * @param region the region to add the handle to
     */
    public static void makeResizableLeft(Region region) {
        makeResizable(region, true);
    }

    private void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
        startX = 0;
        startWidth = 0;
    }

    private void mouseOver(MouseEvent event) {
        if(isInDraggableZone(event) || dragging)
            region.setCursor(Cursor.H_RESIZE);
        else
            region.setCursor(Cursor.DEFAULT);
    }

    private boolean isInDraggableZone(MouseEvent event) {
        if(dragFromLeft)
            return event.getX() < RESIZE_MARGIN;
        return event.getX() > (region.getWidth() - RESIZE_MARGIN);
    }

    private void mouseDragged(MouseEvent event) {
        if(!dragging)
            return;
        var mousex = event.getX();
        var dx = (mousex - startX) * scalar;
        var newWidth = startWidth + dx;
        region.setPrefWidth(newWidth);
    }

    private void mousePressed(MouseEvent event) {
        if(!isInDraggableZone(event))
            return;
        startX = event.getX();
        startWidth = region.getWidth();
        dragging = true;
        // make sure that the minimum Width is set to the current Width once,
        // setting a min Width that is smaller than the current Width will
        // have no effect
        if (!initMinWidth) {
            region.setMinWidth(region.getWidth());
            initMinWidth = true;
        }
    }
}

