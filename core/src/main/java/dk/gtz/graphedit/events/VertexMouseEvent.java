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
 */
public record VertexMouseEvent(
	MouseEvent event,
	UUID vertexId,
	ViewModelVertex vertex,
	Affine viewportAffine,
	ISyntaxFactory syntax,
	ViewModelGraph graph,
	ViewModelEditorSettings editorSettings) {}

