package dk.gtz.graphedit.plugins.syntaxes.petrinet.model;

import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelVertex;

public class ModelPlace extends ModelVertex {
    public int initialTokenCount;

    public int initialTokenCount() {
        return initialTokenCount;
    }

    public ModelPlace() {
        this(new ModelPoint(0,0));
    }

    public ModelPlace(ModelPoint position) {
        this(position, 0);
    }

    public ModelPlace(int initialTokenCount) {
        this(new ModelPoint(0,0), initialTokenCount);
    }

    public ModelPlace(ModelPoint position, int initialTokenCount) {
        super(position);
        this.initialTokenCount = initialTokenCount;
    }
}

