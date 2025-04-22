package dk.gtz.graphedit.model;

import java.util.UUID;

/**
 * An edge from some source to some target. May connect any syntactic element
 *
 * Can be overridden with custom model data if needed.
 */
public class ModelEdge {
	/**
	 * Id of the vertex that the edge originates from
	 */
	public UUID source;
	/**
	 * Id of the vertex that the edge points to
	 */
	public UUID target;

	/**
	 * Create a new instance with uninitialized source and target
	 */
	public ModelEdge() {
		source = null;
		target = null;
	}

	/**
	 * Create a new instance with a provided source and target
	 * 
	 * @param source Id of the source vertex
	 * @param target Id of the target vertex
	 */
	public ModelEdge(UUID source, UUID target) {
		this.source = source;
		this.target = target;
	}
}
