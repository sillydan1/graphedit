package dk.gtz.graphedit.model;

import java.util.UUID;

public abstract class Edge {
	private final UUID source;
    private final UUID target;

    public Edge(UUID source, UUID target) {
        this.source = source;
        this.target = target;
    }

    public UUID getSource() {
		return source;
	}

	public UUID getTarget() {
		return target;
	}
}

