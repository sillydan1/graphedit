package dk.gtz.graphedit.spi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * Interface class for graph-syntax factories
 */
public interface ISyntaxFactory {
	/**
	 * Get the name of the syntax that this factory generates.
	 * 
	 * @return The name-string of the syntax
	 */
	String getSyntaxName();

	/**
	 * Get a list of names that this syntax has been previously known as.
	 * This is useful if you want to rename your syntax because of name clashes.
	 * 
	 * @return An unmodifiable list of names
	 */
	List<String> getLegacyNames();

	/**
	 * Get the long-form description of the syntax.
	 * 
	 * @return A possibly multiline string with a description of the syntax
	 */
	String getSyntaxDescription();

	/**
	 * Create a new javafx vertex representation
	 * 
	 * @param bufferKey         The key of the buffer that contains the graph that
	 *                          contains this view
	 * @param vertexKey         The primary key of the new vertex representation
	 * @param vertexValue       The viewmodel value of the new vertex representation
	 * @param graph             The parent syntax graph
	 * @param viewportTransform The translational, rotational and scale transform of
	 *                          the related viewport
	 * @return The new vertex javafx representation
	 */
	Node createVertexView(String bufferKey, UUID vertexKey, ViewModelVertex vertexValue, ViewModelGraph graph,
			Affine viewportTransform);

	/**
	 * Create a new viewmodel vertex representation
	 * 
	 * @param vertexKey   The primary key of the new vertex representation
	 * @param vertexValue The model vertex to base on
	 * @return A new instance of a viewmodel vertex representation specific to this
	 *         syntax.
	 */
	ViewModelVertex createVertexViewModel(UUID vertexKey, ModelVertex vertexValue);

	/**
	 * Create a new javafx edge representation
	 * 
	 * @param bufferKey         The key of the buffer that contains the graph that
	 *                          contains this view
	 * @param edgeKey           The primary key of the new edge representation
	 * @param edgeValue         The viewmodel value of the new edge representation
	 * @param graph             The parent syntax graph
	 * @param viewportTransform The translational, rotational and scale transform of
	 *                          the related viewport
	 * @return The new edge javafx representation
	 */
	Node createEdgeView(String bufferKey, UUID edgeKey, ViewModelEdge edgeValue, ViewModelGraph graph,
			Affine viewportTransform);

	/**
	 * Create a new viewmodel edge representation
	 * 
	 * @param edgeKey   The primary key of the new edge representation
	 * @param edgeValue The model edge to base on
	 * @return A new instance of a viewmodel edge representation specific to this
	 *         syntax.
	 */
	ViewModelEdge createEdgeViewModel(UUID edgeKey, ModelEdge edgeValue);

	/**
	 * Get the associated syntax version migrater.
	 * 
	 * @return empty if this syntax does not support version migration. Otherwise an
	 *         instance of the appropriate migrater for this syntax.
	 */
	Optional<ISyntaxMigrater> getMigrater();

	/**
	 * Get a {@link IToolbox} with all the appropriate {@link ITool} instances used
	 * to create syntax elements with.
	 * 
	 * @return empty if no additional tools are required or available
	 */
	Optional<IToolbox> getSyntaxTools();
}
