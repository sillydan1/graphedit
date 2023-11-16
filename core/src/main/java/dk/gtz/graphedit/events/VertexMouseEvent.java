package dk.gtz.graphedit.events;

import java.util.UUID;

import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * When a {@link MouseEvent} occurs on an {@link ViewModelVertex}.
 * @param event The javafx {@link MouseEvent} that ocured
 * @param vertexId The id of the vertex that was interacted with
 * @param vertex The vertex that was interacted with
 * @param viewportAffine The {@link Affine} that controls where the viewport is looking
 * @param syntax The syntax factory associated with the current model
 * @param graph The current model graph
 * @param editorSettings The current editor settings
 */
public record VertexMouseEvent(
	MouseEvent event,
	UUID vertexId,
	ViewModelVertex vertex,
	Affine viewportAffine,
	ISyntaxFactory syntax,
	ViewModelGraph graph,
	String bufferId,
	ViewModelEditorSettings editorSettings) {}

