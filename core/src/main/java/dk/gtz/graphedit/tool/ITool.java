package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import dk.gtz.graphedit.events.EdgeMouseEvent;
import javafx.scene.Node;

/**
 * Interface for implementing graph editing tools.
 * Tools are automatically instantiated and can be selected in the model editor toolbar
 */
public interface ITool {
    /**
     * Get a multiline tooltip string that briefly describes this tool
     * @return a (possibly) multiline string
     */
    Optional<String> getTooltip();

    /**
     * Get the graphical representation for the tool selection button.
     * Typically just a {@link FontIcon}, but can be anything
     * @return a javafx node that will be displayed inside the tool selector button
     */
    Node getGraphic();

    /**
     * Get a multiline string that describe the tool and how to use it
     * @return a (possibly) multiline string
     */
    String getHelpDescription();

    /**
     * Event handler for the case where the model editor viewport has been interacted with with the mouse
     * @param e The event that happened
     */
    void onViewportMouseEvent(ViewportMouseEvent e);

    /**
     * Event handler for the case where the model editor viewport has been interacted with with the keyboard
     * @param e The event that happened
     */
    void onKeyEvent(ViewportKeyEvent e);

    /**
     * Event handler for the case where a vertex has been interacted with with the mouse
     * @param e The event that happened
     */
    void onVertexMouseEvent(VertexMouseEvent e);

    /**
     * Event handler for the case where an edge has been interacted with with the mouse
     * @param e The event that happened
     */
    void onEdgeMouseEvent(EdgeMouseEvent e);
}

