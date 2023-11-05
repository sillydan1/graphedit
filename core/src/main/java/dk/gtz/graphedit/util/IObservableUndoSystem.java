package dk.gtz.graphedit.util;

import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.value.ObservableValue;

public interface IObservableUndoSystem extends IUndoSystem {
    ObservableValue<Undoable> getCurrentUndoableProperty();
}

