package dk.gtz.graphedit.view;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.model.migration.ISyntaxMigrater;
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
     * Get a list of names that this syntax has been previously known as.
     * This is useful if you want to rename your syntax because of name clashes.
     * @return An unmodifiable list of names
     */
    List<String> getLegacyNames();

    /**
     * Get the long-form description of the syntax.
     * @return A possibly multiline string with a description of the syntax
     */
    String getSyntaxDescription();

    /**
     * Create a new javafx vertex representation
     * @param vertexKey The primary key of the new vertex representation
     * @param vertexValue The viewmodel value of the new vertex representation
     * @param creatorController The model editor to attach the vertex to. TODO: Consider removing, or interfacing this
     * @return The new vertex javafx representation
     */
    Node createVertex(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController);

    /**
     * Create a new viewmodel vertex representation
     * @param vertexValue The model vertex to base on
     * @return A new instance of a viewmodel vertex representation specific to this syntax.
     */
    ViewModelVertex createVertex(ModelVertex vertexValue);

    /**
     * Create a new javafx edge representation
     * @param edgeKey The primary key of the new edge representation
     * @param edgeValue The viewmodel value of the new edge representation
     * @param creatorController The model editor to attach the edge to. TODO: Consider removing, or interfacing this
     * @return The new edge javafx representation
     */
    Node createEdge(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController);

    /**
     * Create a new viewmodel edge representation
     * @param edgeValue The model edge to base on
     * @return A new instance of a viewmodel edge representation specific to this syntax.
     */
    ViewModelEdge createEdge(ModelEdge edgeValue);

    /**
     * Get the associated syntax version migrater.
     * @return empty if this syntax does not support version migration. Otherwise an instance of the appropriate migrater for this syntax.
     */
    Optional<ISyntaxMigrater> getMigrater();
}

