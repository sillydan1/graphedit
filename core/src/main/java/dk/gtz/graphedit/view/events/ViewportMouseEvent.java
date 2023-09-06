package dk.gtz.graphedit.view.events;

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
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

