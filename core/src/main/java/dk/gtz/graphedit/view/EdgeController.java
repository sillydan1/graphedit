package dk.gtz.graphedit.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

public class EdgeController extends Group {
    private static Logger logger = LoggerFactory.getLogger(EdgeController.class);
    private final UUID edgeKey;
    private final ViewModelEdge edgeValue;

    public EdgeController(UUID edgeKey, ViewModelEdge edge, ViewModelProjectResource resource) {
	this.edgeKey = edgeKey;
	this.edgeValue = edge;
	initialize(resource);
    }

    private void initialize(ViewModelProjectResource resource) {
	var line = initializeLinePresentation(resource);
	getChildren().addAll(line, initializeLeftArrow(line), initializeRightArrow(line));
    }

    private Line initializeLinePresentation(ViewModelProjectResource resource) {
	var edgePresentation = new Line();
	edgePresentation.getStyleClass().add("stroke-primary");
	var sourceVertex = resource.syntax().vertices().getValue().get(edgeValue.source().getValue());
	var targetVertex = resource.syntax().vertices().getValue().get(edgeValue.target().getValue());
	edgePresentation.startXProperty().bind(sourceVertex.position().getXProperty());
	edgePresentation.startYProperty().bind(sourceVertex.position().getYProperty());
	edgePresentation.endXProperty().bind(targetVertex.position().getXProperty());
	edgePresentation.endYProperty().bind(targetVertex.position().getYProperty());
	return edgePresentation;
    }

    private Node initializeLeftArrow(Line line) {
	var arrowLength = 20;
        var leftArrow = new Line();
	leftArrow.getStyleClass().add("stroke-primary");
        leftArrow.startXProperty().bind(line.endXProperty());
        leftArrow.startYProperty().bind(line.endYProperty());
        leftArrow.endXProperty().bind(line.endXProperty().subtract(arrowLength * 1.5));
        leftArrow.endYProperty().bind(line.endYProperty().subtract(arrowLength / 2));
	rotateWithLine(line, leftArrow, leftArrow.startXProperty(), leftArrow.startYProperty());
        return leftArrow;
    }

    private Node initializeRightArrow(Line line) {
	var arrlowLength = 20;
        var rightArrow = new Line();
	rightArrow.getStyleClass().add("stroke-primary");
        rightArrow.startXProperty().bind(line.endXProperty());
        rightArrow.startYProperty().bind(line.endYProperty());
        rightArrow.endXProperty().bind(line.endXProperty().subtract(arrlowLength * 1.5));
        rightArrow.endYProperty().bind(line.endYProperty().add(arrlowLength / 2));
	rotateWithLine(line, rightArrow, rightArrow.startXProperty(), rightArrow.startYProperty());
        return rightArrow;
    }

    private void rotateWithLine(Line line, Node nodeToTransform, DoubleProperty pivotX, DoubleProperty pivotY) {
	var rotate = new Rotate();
	rotate.pivotXProperty().bind(pivotX);
	rotate.pivotYProperty().bind(pivotY);
	rotate.angleProperty().bind(Bindings.createDoubleBinding(() -> 
		    (Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX()) * 180 / Math.PI),
		    line.endYProperty(), line.endXProperty(), line.startXProperty(), line.startYProperty()));
	nodeToTransform.getTransforms().add(rotate);
    }
}

