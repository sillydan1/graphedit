package dk.gtz.graphedit.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.view.util.BindingsUtil;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import dk.yalibs.yadi.DI;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class EdgeController extends Group {
    private static record PointShape(ViewModelPoint point, ViewModelVertexShape shape) {}
    private static Logger logger = LoggerFactory.getLogger(EdgeController.class);
    protected final MouseTracker tracker;
    protected final UUID edgeKey;
    protected final ViewModelEdge edgeValue;
    protected final ViewModelProjectResource resource;
    protected final Affine viewportAffine;
    protected final Line line, lineArrowLeft, lineArrowRight;
    protected final Line selectionHelperLine;
    protected ISyntaxFactory syntaxFactory;

    public EdgeController(UUID edgeKey, ViewModelEdge edge, ViewModelProjectResource resource, Affine viewportAffine, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory) {
	this.edgeKey = edgeKey;
	this.edgeValue = edge;
	this.viewportAffine = viewportAffine;
	this.tracker = DI.get(MouseTracker.class);
	this.resource = resource;
	this.syntaxFactory = syntaxFactory;
	this.line = initialize(selectedTool, editorSettings);
	this.lineArrowLeft = initializeLeftArrow(line);
	this.lineArrowRight = initializeRightArrow(line);
	this.selectionHelperLine = initializeSelectionHelperLine();
	getChildren().addAll(selectionHelperLine, line, lineArrowRight, lineArrowLeft);
	initializeStyle();
    }

    private Line initializeSelectionHelperLine() {
	var line = initializeLinePresentation();
	line.setStrokeWidth(15);
	line.setStroke(Color.TRANSPARENT);
	return line;
    }

    private Line initialize(ObjectProperty<ITool> selectedTool, ViewModelEditorSettings editorSettings) {
	var line = initializeLinePresentation();
	line.getStyleClass().add("stroke-primary");
	initializeEdgeEventHandlers(selectedTool, editorSettings);
	initializeBindPointChangeHandlers();
	return line;
    }

    private PointShape getPointShape(UUID lookupId) {
	if(lookupId.equals(tracker.getTrackerUUID()))
	    return new PointShape(new ViewModelPoint(
			BindingsUtil.createAffineOffsetXBinding(tracker.getXProperty(), viewportAffine),
			BindingsUtil.createAffineOffsetYBinding(tracker.getYProperty(), viewportAffine)),
		    new ViewModelVertexShape(1,1,10,10,ViewModelShapeType.OVAL));
	var sourceVertex = resource.syntax().vertices().getValue().get(lookupId);
	return new PointShape(sourceVertex.position(), sourceVertex.shape());
    }

    private Line initializeLinePresentation() {
	var edgePresentation = new Line();
	var source = getPointShape(edgeValue.source().get());
	var target = getPointShape(edgeValue.target().get());
	edgePresentation.startXProperty().bind(BindingsUtil.createShapedXBinding(target.point(), source.point(), source.shape()));
	edgePresentation.startYProperty().bind(BindingsUtil.createShapedYBinding(target.point(), source.point(), source.shape()));
	edgePresentation.endXProperty().bind(BindingsUtil.createShapedXBinding(source.point(), target.point(), target.shape()));
	edgePresentation.endYProperty().bind(BindingsUtil.createShapedYBinding(source.point(), target.point(), target.shape()));
	return edgePresentation;
    }

    private Line initializeLeftArrow(Line line) {
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

    private Line initializeRightArrow(Line line) {
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
	addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onEdgeMouseEvent(new EdgeMouseEvent(e, edgeKey, edgeValue, viewportAffine, syntaxFactory, resource.syntax(), editorSettings)));
	edgeValue.getIsSelected().addListener((e,o,n) -> {
	    if(n) {
		line.getStyleClass().add("stroke-selected");
		lineArrowLeft.getStyleClass().add("stroke-selected");
		lineArrowRight.getStyleClass().add("stroke-selected");
	    } else {
		line.getStyleClass().remove("stroke-selected");
		lineArrowLeft.getStyleClass().remove("stroke-selected");
		lineArrowRight.getStyleClass().remove("stroke-selected");
	    }
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
	line.startXProperty().bind(BindingsUtil.createShapedXBinding(target.point(), source.point(), source.shape()));
	line.startYProperty().bind(BindingsUtil.createShapedYBinding(target.point(), source.point(), source.shape()));
	line.endXProperty().bind(BindingsUtil.createShapedXBinding(source.point(), target.point(), target.shape()));
	line.endYProperty().bind(BindingsUtil.createShapedYBinding(source.point(), target.point(), target.shape()));
	selectionHelperLine.startXProperty().bind(BindingsUtil.createShapedXBinding(target.point(), source.point(), source.shape()));
	selectionHelperLine.startYProperty().bind(BindingsUtil.createShapedYBinding(target.point(), source.point(), source.shape()));
	selectionHelperLine.endXProperty().bind(BindingsUtil.createShapedXBinding(source.point(), target.point(), target.shape()));
	selectionHelperLine.endYProperty().bind(BindingsUtil.createShapedYBinding(source.point(), target.point(), target.shape()));
    }

    private void initializeStyle() {
	addCursorHoverEffect();
    }

    private void addCursorHoverEffect() {
	setCursor(Cursor.HAND);
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> line.getStyleClass().add("stroke-hover"));
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> lineArrowLeft.getStyleClass().add("stroke-hover"));
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> lineArrowRight.getStyleClass().add("stroke-hover"));

	addEventHandler(MouseEvent.MOUSE_EXITED, event -> line.getStyleClass().remove("stroke-hover"));
	addEventHandler(MouseEvent.MOUSE_EXITED, event -> lineArrowLeft.getStyleClass().remove("stroke-hover"));
	addEventHandler(MouseEvent.MOUSE_EXITED, event -> lineArrowRight.getStyleClass().remove("stroke-hover"));
    }
}

