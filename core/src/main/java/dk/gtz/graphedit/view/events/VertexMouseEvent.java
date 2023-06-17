package dk.gtz.graphedit.view.events;

import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public record VertexMouseEvent(MouseEvent event, ViewModelVertex vertex, Affine viewportAffine) {}

