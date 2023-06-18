package dk.gtz.graphedit.view;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

public class MapGroup<K> {
    private final Group group;
    private final Map<K,Node> childrenMap;

    public MapGroup() {
	this.group = new Group();
	this.childrenMap = new HashMap<>();
    }

    /**
     * Will provide the underlying Group node.
     * Modifying the underlying node's children list is at your own risk
     * @return the managed Group
     */
    public final Group getGroup() {
	return group;
    }

    public ObservableList<Transform> getTransforms() {
	return group.getTransforms();
    }

    public ObservableList<Node> getChildrenUnmodifiable() {
	return group.getChildrenUnmodifiable();
    }

    public Node getChild(K key) {
	return childrenMap.get(key);
    }

    public boolean removeChild(K key) {
	return group.getChildren().remove(childrenMap.get(key));
    }

    public boolean contains(K key) {
	return childrenMap.containsKey(key);
    }

    public boolean addChild(K key, Node child) {
	return addChildren(Map.of(key,child));
    }

    public boolean addChildren(Map<K,Node> children) {
	childrenMap.putAll(children);
	return group.getChildren().addAll(children.values());
    }
}

