package dk.gtz.graphedit.util;

import dk.yalibs.yaundo.Undoable;

/**
 * A record to store an undoable action with a representation.
 * Useful for text-representations for the undo collection.
 * @param representation The text representation of the undoable action.
 * @param undoable The undoable action.
 */
public record ObservableUndoable(String representation, Undoable undoable) {
    @Override
    public String toString() {
	return representation;
    }
}
