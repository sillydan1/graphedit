package dk.gtz.graphedit.view.util;

import java.util.ArrayList;
import java.util.List;

import dk.yalibs.yaundo.Undoable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class ObservableStackUndoSystem implements IObservableUndoSystem {
    private int cursorIndex;
    private final List<Undoable> actions;
    private ObjectProperty<Undoable> currentAction;

    public ObservableStackUndoSystem() {
	this.actions = new ArrayList<>();
        this.cursorIndex = -1;
	this.currentAction = new SimpleObjectProperty<>(null);
    }

    @Override
    public Iterable<Undoable> getHistory() {
        return actions;
    }

    @Override
    public void push(Undoable action) {
        if(actions.size()-1 > cursorIndex)
            removeInRange(actions, cursorIndex+1, actions.size()-1);
        actions.add(++cursorIndex, action);
	currentAction.set(actions.get(cursorIndex));
    }

    @Override
    public void undo() {
        if(cursorIndex-1 < -1)
            return;
        actions.get(cursorIndex--).undo();
	if(cursorIndex == -1)
	    currentAction.set(null);
	else
	    currentAction.set(actions.get(cursorIndex));
    }

    @Override
    public void redo() {
        if(cursorIndex+1 > actions.size()-1)
            return;
        actions.get(++cursorIndex).redo();
	currentAction.set(actions.get(cursorIndex));
    }

    private <T> void removeInRange(List<T> list, int startInd, int endInd) {
        for(var i = endInd; i >= startInd; i--)
            list.remove(i);
    }

    @Override
    public ObservableValue<Undoable> getCurrentUndoableProperty() {
	return currentAction;
    }
}

