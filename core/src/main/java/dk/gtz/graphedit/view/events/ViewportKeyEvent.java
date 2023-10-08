package dk.gtz.graphedit.view.events;

import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Affine;

/**
 * When a {@link KeyEvent} occurs on the model editor viewport
 */
public record ViewportKeyEvent(
		KeyEvent event,
		Affine viewportAffine,
		ISyntaxFactory syntax,
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

