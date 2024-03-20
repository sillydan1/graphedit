package dk.gtz.graphedit.util;

import dk.yalibs.yaundo.Undoable;

public record ObservableUndoable(String representation, Undoable undoable) {
    @Override
    public String toString() {
	return representation;
    }
}
