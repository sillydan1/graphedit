package dk.gtz.graphedit.view.events;

import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Affine;

public record ViewportKeyEvent(
		KeyEvent event,
		Affine viewportAffine,
		ViewModelGraph graph,
		ViewModelEditorSettings editorSettings) {}

