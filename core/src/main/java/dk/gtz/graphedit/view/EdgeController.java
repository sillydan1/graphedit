package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.events.EdgeMouseEvent;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.util.BindingsUtil;
import dk.gtz.graphedit.util.MouseTracker;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import dk.yalibs.yadi.DI;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

/**
 * The view baseclass for graph edges.
 * Contains all the logic needed for the demonstration syntax.
 * If you extend from this and dont want all the features, you should overwrite the unwanted initialize functions
 */
public class EdgeController extends Group {
    private static record PointShape(ViewModelPoint point, ViewModelVertexShape shape) {}
    /**
     * A construct that tracks the mouse position. Useful when the edge is being edited
     */
    protected final MouseTracker tracker;
    /**
     * The edge id
     */
    protected final UUID edgeKey;
    /**
     * The viewmodel value of this edge
     */
    protected final ViewModelEdge edgeValue;
    /**
     * The parent graph
     */
    protected final ViewModelGraph parentGraph;
    /**
     * The affine matrix relating to this edge
     */
    protected final Affine viewportAffine;
    /**
     * The main line
     */
    protected final Line line;
    /**
     * The left side of the arrowhead
     */
    protected final Line lineArrowLeft;
    /**
     * The right side of the arrowhead
     */
    protected final Line lineArrowRight;
    /**
     * A slightly thicker invisible line that helps edge selection
     */
    protected final Line selectionHelperLine;
    /**
     * The associated syntax factory
     */
    protected ISyntaxFactory syntaxFactory;

    /**
     * Constructs a new edgecontroller view component
     * @param edgeKey The id of the edge
     * @param edge The viewmodel data of the edge
     * @param parentGraph The parent graph containing the edge
     * @param viewportAffine The affine matrix relating to the edge
     * @param editorSettings The current editor settings
     * @param selectedTool The object property specifying which tool is currently selected
     * @param syntaxFactory The associated syntax factory
     */
    public EdgeController(UUID edgeKey, ViewModelEdge edge, ViewModelGraph parentGraph, Affine viewportAffine, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory, String bufferKey) {
	this.edgeKey = edgeKey;
	this.edgeValue = edge;
	this.viewportAffine = viewportAffine;
	this.tracker = DI.get(MouseTracker.class);
	this.parentGraph = parentGraph;
	this.syntaxFactory = syntaxFactory;
	this.line = initialize(selectedTool, editorSettings, bufferKey);
	this.lineArrowLeft = initializeLeftArrow(line);
	this.lineArrowRight = initializeRightArrow(line);
	this.selectionHelperLine = initializeSelectionHelperLine();
	var t = new Tooltip();
	this.edgeValue.addHoverListener((e,o,n) -> {
	    if(n == null)
		Tooltip.uninstall(this, t);
	    else {
		t.setGraphic(n);
		Tooltip.install(this, t);
	    }
	});
	getChildren().addAll(selectionHelperLine, line, lineArrowRight, lineArrowLeft);
	initializeStyle();
    }

    private Line initializeSelectionHelperLine() {
	var line = initializeLinePresentation();
	line.setStrokeWidth(15);
	line.setStroke(Color.TRANSPARENT);
	return line;
    }

    private Line initialize(ObjectProperty<ITool> selectedTool, ViewModelEditorSettings editorSettings, String bufferKey) {
	var line = initializeLinePresentation();
	line.getStyleClass().add("stroke-primary");
	initializeEdgeEventHandlers(selectedTool, editorSettings, bufferKey);
	initializeBindPointChangeHandlers();
	return line;
    }

    private PointShape getPointShape(UUID lookupId) {
	if(lookupId.equals(tracker.getTrackerUUID()))
	    return new PointShape(new ViewModelPoint(
			BindingsUtil.createAffineOffsetXBinding(tracker.getXProperty(), viewportAffine),
			BindingsUtil.createAffineOffsetYBinding(tracker.getYProperty(), viewportAffine)),
		    new ViewModelVertexShape(1,1,10,10,ViewModelShapeType.OVAL));
	var sourceVertex = parentGraph.vertices().getValue().get(lookupId);
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

    private void initializeEdgeEventHandlers(ObjectProperty<ITool> selectedTool, ViewModelEditorSettings editorSettings, String bufferKey) {
	addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onEdgeMouseEvent(new EdgeMouseEvent(e, edgeKey, edgeValue, viewportAffine, syntaxFactory, parentGraph, bufferKey, editorSettings)));
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
