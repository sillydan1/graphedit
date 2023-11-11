package dk.gtz.graphedit.plugins.syntaxes.petrinet.model;

import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;

public class ModelArc extends ModelEdge {
    public int weight;

    public ModelArc() {
        this(1);
    }

    public ModelArc(int weight) {
        super();
        this.weight = weight;
    }

    public ModelArc(UUID source, UUID target) {
        this(source, target, 1);
    }

    public ModelArc(UUID source, UUID target, int weight) {
        super(source, target);
        this.weight = weight;
    }
}

