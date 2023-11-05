package dk.gtz.graphedit.events;

import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * When a {@link MouseEvent} occurs on the model editor viewport
 */
public record ViewportMouseEvent(
		MouseEvent event,
		Affine viewportAffine,
		boolean isTargetDrawPane,
		ISyntaxFactory syntax,
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

