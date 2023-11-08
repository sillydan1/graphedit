package dk.gtz.graphedit.plugins.syntaxes.petrinet.model;

import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelVertex;

public class ModelTransition extends ModelVertex {
    public ModelTransition() {
        this(new ModelPoint(0,0));
    }

    public ModelTransition(ModelPoint position) {
        super(position);
    }
}

