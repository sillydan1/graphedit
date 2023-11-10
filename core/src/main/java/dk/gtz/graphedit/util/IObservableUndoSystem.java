package dk.gtz.graphedit.util;

import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.value.ObservableValue;

/**
 * Interface for an undo system where the {@link Undoable} entries are observable
 */
public interface IObservableUndoSystem extends IUndoSystem {
    /**
     * Get the current {@link Undoable} observable value
     * @return The current undoable
     */
    ObservableValue<Undoable> getCurrentUndoableProperty();
}
