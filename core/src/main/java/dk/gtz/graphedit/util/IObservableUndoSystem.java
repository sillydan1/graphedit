package dk.gtz.graphedit.util;

import java.util.ArrayList;
import java.util.List;

import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.value.ChangeListener;
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

    default List<ObservableUndoable> getStringRepresentation() {
        var result = new ArrayList<ObservableUndoable>();
        var current = getCurrentAction();
        for(var undoable : getHistory()) {
            if(current.isPresent() && undoable == current.get())
                result.add(new ObservableUndoable("* [" + undoable.getDescription() + "]", undoable));
            else
                result.add(new ObservableUndoable(" * " + undoable.getDescription(), undoable));
        }
        if(result.isEmpty())
            result.add(new ObservableUndoable("<empty>", null));
        return result;
    }

    void addListener(ChangeListener<Undoable> listener);

    void removeListener(ChangeListener<Undoable> listener);

    void gotoAction(Undoable action);
}
