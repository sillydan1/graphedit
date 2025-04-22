package dk.gtz.graphedit.viewmodel;

import javafx.beans.property.BooleanProperty;

/**
 * Interface for elements that are selectable through the selection system
 */
public interface ISelectable {
	/**
	 * Get the observable property that determines if the object is selected or not.
	 * This is useful if you want to add event listeners to handle when the property
	 * changes.
	 * 
	 * @return {@code true} if the object is currently selected, otherwise
	 *         {@code false}
	 */
	BooleanProperty getIsSelected();

	/**
	 * Select the object
	 */
	void select();

	/**
	 * Deselect the object
	 */
	void deselect();
}
