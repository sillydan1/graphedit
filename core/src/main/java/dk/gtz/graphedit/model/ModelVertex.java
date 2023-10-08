package dk.gtz.graphedit.model;

/**
 * The base class of a graph vertex.
 */
public class ModelVertex {
    public ModelPoint position;

    public ModelPoint position() {
        return position;
    }

    public ModelVertex() {
        this(new ModelPoint(0,0));
    }

    public ModelVertex(ModelPoint position) {
        this.position = position;
    }
}

