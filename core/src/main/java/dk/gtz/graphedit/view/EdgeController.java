package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class EdgeController extends Group {
    private final UUID edgeKey;
    private final ViewModelEdge edgeValue;

    public EdgeController(UUID edgeKey, ViewModelEdge edge, ViewModelProjectResource resource) {
	this.edgeKey = edgeKey;
	this.edgeValue = edge;
	initialize(resource);
    }

    private void initialize(ViewModelProjectResource resource) {
	getChildren().add(initializeLinePresentation(resource));
    }

    private Line initializeLinePresentation(ViewModelProjectResource resource) {
	var edgePresentation = new Line();
	edgePresentation.setStroke(Color.WHITE);
	// subscribe on events
	var sourceVertex = resource.syntax().vertices().getValue().get(edgeValue.source().getValue());
	var targetVertex = resource.syntax().vertices().getValue().get(edgeValue.target().getValue());
	edgePresentation.startXProperty().bind(sourceVertex.position().getXProperty());
	edgePresentation.startYProperty().bind(sourceVertex.position().getYProperty());
	edgePresentation.endXProperty().bind(targetVertex.position().getXProperty());
	edgePresentation.endYProperty().bind(targetVertex.position().getYProperty());
	return edgePresentation;
    }

}

