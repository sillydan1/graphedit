package dk.gtz.graphedit.events;

import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Affine;

/**
 * When a {@link KeyEvent} occurs on the model editor viewport
 * @param event The javafx {@link KeyEvent} that ocured
 * @param viewportAffine The {@link Affine} that controls where the viewport is looking
 * @param isTargetDrawpane If true, then this event is targeting the drawpane. Useful for filtering unwanted events
 * @param syntax The syntax factory associated with the current model
 * @param graph The current model graph
 * @param editorSettings The current editor settings
 */
public record ViewportKeyEvent(
		KeyEvent event,
		Affine viewportAffine,
		boolean isTargetDrawpane,
		ISyntaxFactory syntax,
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

