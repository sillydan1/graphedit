package dk.gtz.graphedit.view.events;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public record EdgeMouseEvent(
		MouseEvent event,
		UUID edgeId,
		ViewModelEdge edge,
		Affine viewportAffine,
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

