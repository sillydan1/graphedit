package dk.gtz.graphedit.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

import dk.yalibs.yaundo.Undoable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ObservableTreeUndoSystem implements IObservableUndoSystem {
    private static class Node implements Iterable<Undoable> {
	private final Undoable action;
	private final Optional<Node> parent;
	private final List<Node> children;

	public Node(Undoable action, Optional<Node> parent) {
	    this.action = action;
	    this.parent = parent;
	    this.children = new LinkedList<>();
	}

	private static class BfsIterator implements Iterator<Undoable> {
	    private final Queue<Node> queue;

	    public BfsIterator(Node root) {
		this.queue = new LinkedList<>();
		if(!root.children.isEmpty())
		    queue.addAll(root.children);
	    }

	    @Override
	    public boolean hasNext() {
		return !queue.isEmpty();
	    }

	    @Override
	    public Undoable next() {
		if(!hasNext())
		    throw new NoSuchElementException();
		var x = queue.poll();
		x.children.forEach(queue::add);
		return x.action;
	    }
	}

	@Override
	public Iterator<Undoable> iterator() {
	    return new BfsIterator(this);
	}
    }

    private final Node root;
    private final ObjectProperty<Undoable> currentAction;
    private final ObjectProperty<Node> currentNode;
    private final List<ChangeListener<Undoable>> listeners;

    public ObservableTreeUndoSystem() {
	this.root = new Node(null, Optional.empty());
	this.currentNode = new SimpleObjectProperty<>(root);
	this.currentAction = new SimpleObjectProperty<>(root.action);
	this.listeners = new ArrayList<>();
    }

    @Override
    public Iterable<Undoable> getHistory() {
	return root;
    }

    @Override
    public Optional<Undoable> getCurrentAction() {
	return Optional.ofNullable(currentAction.get());
    }

    @Override
    public void push(Undoable action) {
	var newNode = new Node(action, Optional.of(currentNode.get()));
	var oldNode = currentNode.get();
	oldNode.children.add(newNode);
	currentNode.set(newNode);
	currentAction.set(newNode.action);
	listeners.forEach(l -> l.changed(currentAction, oldNode.action, currentAction.get()));
    }

    @Override
    public void undo() {
	if(currentNode.get() == root)
	    return;
	var prev = currentNode.get();
	prev.action.undo();
	currentNode.set(prev.parent.orElseThrow());
	// NOTE: add prev to the beginning of the list of children
	// NOTE: so we will redo the most recently undone branch
	currentNode.get().children.remove(prev);
	currentNode.get().children.add(0, prev);
	currentAction.set(currentNode.get().action);
    }

    @Override
    public void redo() {
	if(currentNode.get().children.isEmpty())
	    return;
	var next = currentNode.get().children.get(0);
	next.action.redo();
	currentNode.set(next);
	currentAction.set(currentNode.get().action);
    }

    @Override
    public ObservableValue<Undoable> getCurrentUndoableProperty() {
	return currentAction;
    }

    @Override
    public void addListener(ChangeListener<Undoable> listener) {
	listeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener<Undoable> listener) {
	listeners.remove(listener);
    }
}
