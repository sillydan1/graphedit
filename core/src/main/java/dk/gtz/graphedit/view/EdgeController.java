package dk.gtz.graphedit.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.view.util.BindingsUtil;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class EdgeController extends Group {
    private static record PointShape(ViewModelPoint point, ViewModelVertexShape shape) {}
    private static Logger logger = LoggerFactory.getLogger(EdgeController.class);
    private final MouseTracker tracker;
    private final UUID edgeKey;
    private final ViewModelEdge edgeValue;
    private final ViewModelProjectResource resource;
    private final Affine viewportAffine;
    private final Line line;

    public EdgeController(UUID edgeKey, ViewModelEdge edge, ViewModelProjectResource resource, Affine viewportAffine, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool) {
	this.edgeKey = edgeKey;
	this.edgeValue = edge;
	this.viewportAffine = viewportAffine;
	this.tracker = DI.get(MouseTracker.class);
	this.resource = resource;
	this.line = initialize(selectedTool, editorSettings);
    }

    private Line initialize(ObjectProperty<ITool> selectedTool, ViewModelEditorSettings editorSettings) {
	var line = initializeLinePresentation();
	getChildren().addAll(line, initializeLeftArrow(line), initializeRightArrow(line));
	initializeEdgeEventHandlers(selectedTool, editorSettings);
	initializeBindPointChangeHandlers();
	return line;
    }

    private PointShape getPointShape(UUID lookupId) {
	if(lookupId.equals(tracker.getTrackerUUID()))
	    return new PointShape(new ViewModelPoint(
			// TODO: This subtract and divide stuff should be extracted into BindingsUtil
			Bindings.createDoubleBinding(() -> tracker.getXProperty().subtract(viewportAffine.getTx()).divide(viewportAffine.getMxx()).get(), tracker.getXProperty()),
			Bindings.createDoubleBinding(() -> tracker.getYProperty().subtract(viewportAffine.getTy()).divide(viewportAffine.getMyy()).get(), tracker.getYProperty())),
		    new ViewModelVertexShape(1,1,10,10,ViewModelShapeType.OVAL));
	var sourceVertex = resource.syntax().vertices().getValue().get(lookupId);
	return new PointShape(sourceVertex.position(), sourceVertex.shape());
    }

    private Line initializeLinePresentation() {
	var edgePresentation = new Line();
	edgePresentation.getStyleClass().add("stroke-primary");
	var source = getPointShape(edgeValue.source().get());
	var target = getPointShape(edgeValue.target().get());
	edgePresentation.startXProperty().bind(BindingsUtil.createOvalXBinding(target.point(), source.point(), source.shape()));
	edgePresentation.startYProperty().bind(BindingsUtil.createOvalYBinding(target.point(), source.point(), source.shape()));
	edgePresentation.endXProperty().bind(BindingsUtil.createOvalXBinding(source.point(), target.point(), target.shape()));
	edgePresentation.endYProperty().bind(BindingsUtil.createOvalYBinding(source.point(), target.point(), target.shape()));
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

    private void initializeEdgeEventHandlers(ObjectProperty<ITool> selectedTool, ViewModelEditorSettings editorSettings) {
	// TODO: Make it easier to click on edges
	addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onEdgeMouseEvent(new EdgeMouseEvent(e, edgeKey, edgeValue, viewportAffine, resource.syntax(), editorSettings)));
	// TODO: This only highlights the main line. Not the arrow-part. Make it do that.
	edgeValue.getIsSelected().addListener((e,o,n) -> {
	    if(n)
		line.getStyleClass().add("stroke-primary-selected");
	    else
		line.getStyleClass().remove("stroke-primary-selected");
	});
    }

    private void initializeBindPointChangeHandlers() {
	edgeValue.source().addListener((e,o,n) -> onChangeSourceBindPoint(n));
	edgeValue.target().addListener((e,o,n) -> onChangeTargetBindPoint(n));
    }

    private void onChangeSourceBindPoint(UUID newSource) {
	changeTargetBindPoints(newSource, edgeValue.target().get());
    }

    private void onChangeTargetBindPoint(UUID newTarget) {
	changeTargetBindPoints(edgeValue.source().get(), newTarget);
    }

    private void changeTargetBindPoints(UUID sourceId, UUID targetId) {
	var source = getPointShape(sourceId);
	var target = getPointShape(targetId);
	line.startXProperty().bind(BindingsUtil.createOvalXBinding(target.point(), source.point(), source.shape()));
	line.startYProperty().bind(BindingsUtil.createOvalYBinding(target.point(), source.point(), source.shape()));
	line.endXProperty().bind(BindingsUtil.createOvalXBinding(source.point(), target.point(), target.shape()));
	line.endYProperty().bind(BindingsUtil.createOvalYBinding(source.point(), target.point(), target.shape()));
    }
}

