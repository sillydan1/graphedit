package dk.gtz.graphedit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dk.yalibs.yaundo.Undoable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Implementation of an observable undosystem
 */
public class ObservableStackUndoSystem implements IObservableUndoSystem {
	private int cursorIndex;
	private final List<Undoable> actions;
	private ObjectProperty<Undoable> currentAction;
	private final List<ChangeListener<Undoable>> listeners;

	/**
	 * Construct a new instance
	 */
	public ObservableStackUndoSystem() {
		this.actions = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.cursorIndex = -1;
		this.currentAction = new SimpleObjectProperty<>(null);
	}

	@Override
	public Iterable<Undoable> getHistory() {
		return actions;
	}

	@Override
	public void push(Undoable action) {
		if (actions.size() - 1 > cursorIndex)
			removeInRange(actions, cursorIndex + 1, actions.size() - 1);
		var oldAction = cursorIndex == -1 ? null : actions.get(cursorIndex);
		actions.add(++cursorIndex, action);
		currentAction.set(actions.get(cursorIndex));
		listeners.forEach(l -> l.changed(currentAction, oldAction, currentAction.get()));
	}

	@Override
	public void undo() {
		if (cursorIndex - 1 < -1)
			return;
		actions.get(cursorIndex--).undo();
		if (cursorIndex == -1)
			currentAction.set(null);
		else
			currentAction.set(actions.get(cursorIndex));
	}

	@Override
	public void redo() {
		if (cursorIndex + 1 > actions.size() - 1)
			return;
		actions.get(++cursorIndex).redo();
		currentAction.set(actions.get(cursorIndex));
	}

	private <T> void removeInRange(List<T> list, int startInd, int endInd) {
		for (var i = endInd; i >= startInd; i--)
			list.remove(i);
	}

	@Override
	public ObservableValue<Undoable> getCurrentUndoableProperty() {
		return currentAction;
	}

	@Override
	public Optional<Undoable> getCurrentAction() {
		return Optional.ofNullable(currentAction.get());
	}

	@Override
	public void addListener(ChangeListener<Undoable> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ChangeListener<Undoable> listener) {
		listeners.remove(listener);
	}

	@Override
	public void gotoAction(Undoable action) {
		throw new UnsupportedOperationException("Unimplemented method 'gotoAction'");
	}
}
