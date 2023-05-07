package dk.gtz.graphedit.model;

import java.util.List;
import java.util.UUID;

public class TaggableEdge extends Edge {
    private final List<UUID> tags;

	public TaggableEdge(UUID source, UUID target, List<UUID> tags) {
		super(source, target);
        this.tags = tags;
	}

	public List<UUID> getTags() {
		return tags;
	}
}

