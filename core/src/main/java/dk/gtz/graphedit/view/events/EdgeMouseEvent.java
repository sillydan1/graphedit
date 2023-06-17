package dk.gtz.graphedit.view.events;

import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public record EdgeMouseEvent(MouseEvent event, ViewModelEdge edge, Affine viewportAffine) {}

