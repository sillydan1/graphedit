package dk.gtz.graphedit.view.events;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public record VertexMouseEvent(MouseEvent event, UUID vertexId, ViewModelVertex vertex, Affine viewportAffine, ViewModelGraph graph) {}

