package dk.gtz.graphedit.view;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Interface for handling mouse and key events.
 */
public interface IEventHandler {
    /**
     * Handles a mouse event.
     * @param event The mouse event to handle.
     */
    void onMouseEvent(MouseEvent event);
    /**
     * Handles a key event.
     * @param event The key event to handle.
     */
    void onKeyEvent(KeyEvent event);
}
