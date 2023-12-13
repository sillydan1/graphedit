package dk.gtz.graphedit.events;

import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * When a {@link MouseEvent} occurs on the model editor viewport
 * @param event The javafx {@link MouseEvent} that ocured
 * @param viewportAffine The {@link Affine} that controls where the viewport is looking
 * @param isTargetDrawPane If true, then this event is targeting the drawpane. Useful for filtering unwanted events
 * @param syntax The syntax factory associated with the current model
 * @param graph The current model graph
 * @param bufferId The related buffer key
 * @param editorSettings The current editor settings
 */
public record ViewportMouseEvent(
		MouseEvent event,
		Affine viewportAffine,
		boolean isTargetDrawPane,
		ISyntaxFactory syntax,
		ViewModelGraph graph,
		String bufferId,
		ViewModelEditorSettings editorSettings) {}

