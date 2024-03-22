package dk.gtz.graphedit.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.yalibs.yaundo.Undoable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ObservableTreeUndoSystem implements IObservableUndoSystem {
    private static final Logger logger = LoggerFactory.getLogger(ObservableTreeUndoSystem.class);

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
		queue.addAll(x.children);
		return x.action;
	    }
	}

	private static class NodeIterator implements Iterator<Node> {
	    private final Queue<Node> queue;

	    public NodeIterator(Node root) {
		this.queue = new LinkedList<>();
		if(!root.children.isEmpty())
		    queue.addAll(root.children);
	    }

	    @Override
	    public boolean hasNext() {
		return !queue.isEmpty();
	    }

	    @Override
	    public Node next() {
		if(!hasNext())
		    throw new NoSuchElementException();
		var x = queue.poll();
		queue.addAll(x.children);
		return x;
	    }
	}

	@Override
	public Iterator<Undoable> iterator() {
	    return new BfsIterator(this);
	}

	public Iterator<Node> nodeIterator() {
	    return new NodeIterator(this);
	}
    }

    private final Node root;
    private final ObjectProperty<Undoable> currentAction;
    private final ObjectProperty<Node> currentNode;
    private final List<ChangeListener<Undoable>> listeners;
    private Node currentLatest;

    public ObservableTreeUndoSystem() {
	this.root = new Node(null, Optional.empty());
	this.currentNode = new SimpleObjectProperty<>(root);
	this.currentAction = new SimpleObjectProperty<>(root.action);
	this.listeners = new ArrayList<>();
	currentLatest = root;
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
	currentLatest = newNode;
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
	currentAction.set(currentNode.get().action);
    }

    @Override
    public void redo() {
	if(currentNode.get() == currentLatest)
	    return;
	var stack = getAncestors(currentLatest);
	for(var i = 0; i < stack.size(); i++) {
	    if(stack.get(i) == currentNode.get()) {
		redo(currentNode.get().children.indexOf(stack.get(i+1)));
		return;
	    }
	}
	logger.error("current node not found in the ancestors of the current latest node");
    }

    private void redo(int childIndex) {
	if(currentNode.get().children.isEmpty())
	    return;
	var next = currentNode.get().children.get(childIndex);
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

    @Override
    public List<ObservableUndoable> getStringRepresentation() {
	return helper(0, 0, root);
    }

    private List<ObservableUndoable> helper(int indent, int index, Node node) {
	var result = new ArrayList<ObservableUndoable>();
	result.add(getStringRepresentation(indent, index++, node));
	for(var i = 1; i < node.children.size(); i++)
	    result.add(getBranchRepresentation(indent + i, index, node));
	for(var i = node.children.size()-1; i >= 0; i--)
	    result.addAll(helper(indent + i, index, node.children.get(i)));
	return result;
    }

    private ObservableUndoable getBranchRepresentation(int indent, int index, Node parentNode) {
	var spaces = "| ".repeat(indent-1);
	return new ObservableUndoable("%s|/".formatted(spaces), parentNode.action);
    }

    private ObservableUndoable getStringRepresentation(int indent, int index, Node node) {
	var spaces = "| ".repeat(indent);
	var rep = " %d ".formatted(index);
	if(node == currentLatest)
	    rep = "{%d}".formatted(index);
	if(node == currentNode.get())
	    rep = ">%d<".formatted(index);
	if(node == root)
	    return new ObservableUndoable("%s* %s\t(%s)".formatted(spaces, rep, "original"), node.action);
	return new ObservableUndoable("%s* %s\t(%s)".formatted(spaces, rep, node.action.getDescription()), node.action);
    }

    private Optional<Node> find(Undoable action) {
	if(action == null)
	    return Optional.of(root);
	var it = root.nodeIterator();
	while(it.hasNext()) {
	    var node = it.next();
	    if(node.action == action)
		return Optional.of(node);
	}
	return Optional.empty();
    }

    @Override
    public void gotoAction(Undoable action) {
	var actionNode = find(action);
	if(actionNode.isEmpty()) {
	    logger.error("action not found in the tree");
	    return;
	}
	currentLatest = getMostRecentChild(actionNode.get());
	var pathA = getAncestors(currentNode.get());
	var pathB = getAncestors(actionNode.get());
	var lcaIndex = 0;
	for(; lcaIndex < Math.min(pathA.size(), pathB.size()); lcaIndex++)
	    if(pathA.get(lcaIndex) != pathB.get(lcaIndex))
		break;

	for(var x = 0; x < pathA.size() - lcaIndex; x++)
	    undo();

	for(var x = lcaIndex; x < pathB.size(); x++) {
	    var index = getChildIndex(currentNode.get(), pathB.get(x).action);
	    if(index.isEmpty())
		throw new IllegalStateException("Action not found in the tree");
	    redo(index.get());
	}
    }

    private Optional<Integer> getChildIndex(Node node, Undoable action) {
	for(var i = 0; i < node.children.size(); i++)
	    if(node.children.get(i).action == action)
		return Optional.of(i);
	return Optional.empty();
    }

    private List<Node> getAncestors(Node node) {
	var result = new ArrayList<Node>();
	result.add(node);
	while(node != root) {
	    node = node.parent.get();
	    result.add(node);
	}
	Collections.reverse(result);
	return result;
    }

    private Node getMostRecentChild(Node node) {
	if(node.children.isEmpty())
	    return node;
	return getMostRecentChild(node.children.get(0));
    }
}
