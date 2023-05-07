package dk.gtz.graphedit.model;

import java.util.UUID;

/**
 * An edge from some source to some target. May connect any syntactic element
 */
public class Edge {
	private final UUID source;
	private final UUID target;

	public Edge(UUID source, UUID target) {
		this.source = source;
		this.target = target;
	}

	public Edge() {
		this(UUID.randomUUID(), UUID.randomUUID());
	}

	public UUID getSource() {
		return source;
	}

	public UUID getTarget() {
		return target;
	}
}

