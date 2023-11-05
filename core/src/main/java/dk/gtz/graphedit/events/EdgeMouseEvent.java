package dk.gtz.graphedit.events;

import java.util.UUID;

import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * When a {@link MouseEvent} occurs on an {@link ViewModelEdge}.
 */
public record EdgeMouseEvent(
		MouseEvent event,
		UUID edgeId,
		ViewModelEdge edge,
		Affine viewportAffine,
		ISyntaxFactory syntax,
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

