package dk.gtz.graphedit.syntaxes.lts.model;

import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;

public class ModelTransition extends ModelEdge {
    private String action;

    public String action() {
        return action;
    }

    public ModelTransition() {
        super();
        this.action = "tau";
    }

    public ModelTransition(UUID source, UUID target, String action) {
        super(source, target);
        this.action = action;
    }
}

