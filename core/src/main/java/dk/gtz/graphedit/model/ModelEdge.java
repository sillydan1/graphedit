package dk.gtz.graphedit.model;

import java.util.UUID;

/**
 * An edge from some source to some target. May connect any syntactic element
 *
 * Can be overridden with custom model data if needed.
 */
public class ModelEdge {
    public UUID source;
    public UUID target;

    public UUID source() {
        return source;
    }

    public UUID target() {
        return target;
    }

    public ModelEdge() {

    }

    public ModelEdge(UUID source, UUID target) {
        this.source = source;
        this.target = target;
    }
}

