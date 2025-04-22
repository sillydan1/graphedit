package dk.gtz.graphedit.viewmodel;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;

/**
 * Interface for add mouse-hover support.
 */
public interface IHoverable {
	/**
	 * Trigger a hover event and display the provided node in the hover effect.
	 * 
	 * @param hoverDisplay The javafx node to show in the hover window.
	 */
	void hover(Node hoverDisplay);

	/**
	 * Stop showing the hover effect.
	 */
	void unhover();

	/**
	 * Check if the hover effect is currently showing.
	 * 
	 * @return True if the hover effect is currently being shown, false otherwise.
	 */
	boolean isHovering();

	/**
	 * Add a changelistener that will be invoked when {@link IHoverable#hover} is
	 * being called.
	 * 
	 * @param consumer The changelistener to add.
	 */
	void addHoverListener(ChangeListener<Node> consumer);
}
