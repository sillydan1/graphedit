package dk.gtz.graphedit.view.events;

import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public record ViewportMouseEvent(MouseEvent event, Affine viewportAffine, ViewModelGraph graph) {}

