package dk.gtz.graphedit.view.util;

import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.value.ObservableValue;

public interface IObservableUndoSystem extends IUndoSystem {
    ObservableValue<Undoable> getCurrentUndoableProperty();
}

