package dk.gtz.graphedit.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.view.util.BindingsUtil;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class EdgeController extends Group {
    private static Logger logger = LoggerFactory.getLogger(EdgeController.class);
    private final UUID edgeKey;
    private final ViewModelEdge edgeValue;
    private final Affine viewportAffine;

    public EdgeController(UUID edgeKey, ViewModelEdge edge, ViewModelProjectResource resource, Affine viewportAffine, ObjectProperty<ITool> selectedTool) {
	this.edgeKey = edgeKey;
	this.edgeValue = edge;
	this.viewportAffine = viewportAffine;
	initialize(resource, selectedTool);
    }

    private void initialize(ViewModelProjectResource resource, ObjectProperty<ITool> selectedTool) {
	var line = initializeLinePresentation(resource);
	getChildren().addAll(line, initializeLeftArrow(line), initializeRightArrow(line));
	initializeEdgeEventHandlers(selectedTool);
    }

    private Line initializeLinePresentation(ViewModelProjectResource resource) {
	var edgePresentation = new Line();
	edgePresentation.getStyleClass().add("stroke-primary");
	var sourceVertex = resource.syntax().vertices().getValue().get(edgeValue.source().getValue());
	var targetVertex = resource.syntax().vertices().getValue().get(edgeValue.target().getValue());
	var sourcePosition = sourceVertex.position();
	var targetPosition = targetVertex.position();
	edgePresentation.startXProperty().bind(BindingsUtil.createOvalXBinding(targetPosition, sourcePosition, sourceVertex.shape()));
	edgePresentation.startYProperty().bind(BindingsUtil.createOvalYBinding(targetPosition, sourcePosition, sourceVertex.shape()));
	edgePresentation.endXProperty().bind(BindingsUtil.createOvalXBinding(sourcePosition, targetPosition, targetVertex.shape()));
	edgePresentation.endYProperty().bind(BindingsUtil.createOvalYBinding(sourcePosition, targetPosition, targetVertex.shape()));
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
	rotate.angleProperty().bind(BindingsUtil.createRotationAtLineEndBinding(line));
	nodeToTransform.getTransforms().add(rotate);
    }

    private void initializeEdgeEventHandlers(ObjectProperty<ITool> selectedTool) {
	addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onEdgeMouseEvent(new EdgeMouseEvent(e, edgeValue, viewportAffine)));
    }
}

