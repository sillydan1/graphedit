package dk.gtz.graphedit.util;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * A keyed {@link Group} of javafx {@link Node}s
 * 
 * @param <K> The key type
 */
public class MapGroup<K> {
	private final Group group;
	private final Map<K, Node> childrenMap;

	/**
	 * Create a new instance
	 */
	public MapGroup() {
		this.group = new Group();
		this.childrenMap = new HashMap<>();
	}

	/**
	 * Will provide the underlying Group node.
	 * Modifying the underlying node's children list is at your own risk
	 * 
	 * @return the managed Group
	 */
	public final Group getGroup() {
		return group;
	}

	/**
	 * Add a transform to the group
	 * 
	 * @param transform the transform to add
	 */
	public void addTransform(Transform transform) {
		group.getTransforms().add(transform);
	}

	/**
	 * Returns the value associated with the key, or {@code null} if it doesn't
	 * exist
	 * 
	 * @param key The key to lookup
	 * @return The value to which the key is mapped, or {@code null} if it doesn't
	 *         exist
	 */
	public Node getChild(K key) {
		return childrenMap.get(key);
	}

	/**
	 * Removes the map entry associated with the provided key
	 * 
	 * @param key The key of the value to remove from the map
	 * @return {@code true} if the map contained the key, otherwise {@code false}.
	 */
	public boolean removeChild(K key) {
		return group.getChildren().remove(childrenMap.get(key));
	}

	/**
	 * Returns {@code true} if the provided key contains an entry in the map
	 * 
	 * @param key The key to search for
	 * @return {@code true} if the map contains the provided key, otherwise
	 *         {@code false}
	 */
	public boolean contains(K key) {
		return childrenMap.containsKey(key);
	}

	/**
	 * Add a new key/value entry to the map and attach the {@code Node} to the
	 * group.
	 * 
	 * @param key   The key of the map entry
	 * @param child The javafx value of the map entry
	 * @return {@code true} if the group changed as a result of the addition
	 */
	public boolean addChild(K key, Node child) {
		return addChildren(Map.of(key, child));
	}

	/**
	 * Add a collection of key/value entries to the map and attach the {@code Node}s
	 * to the group.
	 * 
	 * @param children A map of key/value entries to add to the group.
	 * @return {@code true} if the group has changed as a result of the addition
	 */
	public boolean addChildren(Map<K, Node> children) {
		childrenMap.putAll(children);
		return group.getChildren().addAll(children.values());
	}
}
