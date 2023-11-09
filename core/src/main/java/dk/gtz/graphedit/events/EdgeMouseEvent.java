package dk.gtz.graphedit.events;

import java.util.UUID;

import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * When a {@link MouseEvent} occurs on an {@link ViewModelEdge}.
 * @param event The javafx {@link MouseEvent} that ocured
 * @param edgeId The id of the edge that was interacted with
 * @param edge The edge object that was interacted with
 * @param viewportAffine The {@link Affine} that controls where the viewport is looking
 * @param syntax The syntax factory associated with the current model
 * @param graph The current model graph
 * @param editorSettings The current editor settings
 */
public record EdgeMouseEvent(
		MouseEvent event,
		UUID edgeId,
		ViewModelEdge edge,
		Affine viewportAffine,
		ISyntaxFactory syntax,
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

