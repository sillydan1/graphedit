package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.Node;

/**
 * Interface class for graph-syntax factories
 */
public interface ISyntaxFactory {
    /**
     * Get the name of the syntax that this factory generates.
     * @return The name-string of the syntax
     */
    String getSyntaxName();
    /**
     * Create a new javafx vertex representation
     * @param vertexKey The primary key of the new vertex representation
     * @param vertexValue The viewmodel value of the new vertex representation
     * @param creatorController The model editor to attach the vertex to. TODO: Consider removing, or interfacing this
     * @return The new vertex javafx representation
     */
    Node createVertex(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController);
    /**
     * Create a new javafx edge representation
     * @param edgeKey The primary key of the new edge representation
     * @param edgeValue The viewmodel value of the new edge representation
     * @param creatorController The model editor to attach the edge to. TODO: Consider removing, or interfacing this
     * @return The new edge javafx representation
     */
    Node createEdge(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController);
}

