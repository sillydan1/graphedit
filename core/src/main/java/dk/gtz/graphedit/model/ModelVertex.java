package dk.gtz.graphedit.model;

/**
 * The base class of a graph vertex.
 *
 * Can be overridden with custom model data if needed.
 */
public class ModelVertex {
    /**
     * The point at which the vertex is located
     */
    public ModelPoint position;

    /**
     * Construct a new instance
     */
    public ModelVertex() {
        this(new ModelPoint(0,0));
    }

    /**
     * Construct a new instance with a provided position
     * @param position Point value for the vertex position
     */
    public ModelVertex(ModelPoint position) {
        this.position = position;
    }
}
