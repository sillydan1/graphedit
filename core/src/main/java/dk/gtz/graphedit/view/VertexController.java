package dk.gtz.graphedit.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

public class VertexController extends StackPane {
    private final Logger logger = LoggerFactory.getLogger(VertexController.class);
    protected final UUID vertexKey;
    protected final ViewModelVertex vertexValue;
    protected final Affine viewportAffine;
    protected Node graphic;

    public VertexController(UUID vertexKey, ViewModelVertex vertex, Affine viewportAffine, ViewModelGraph graph, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool) {
	this.vertexKey = vertexKey;
	this.vertexValue = vertex;
	this.viewportAffine = viewportAffine;
	initialize(selectedTool, graph, editorSettings);
    }

    private void initialize(ObjectProperty<ITool> selectedTool, ViewModelGraph graph, ViewModelEditorSettings editorSettings) {
	this.graphic = initializeVertexRepresentation();
	getChildren().add(graphic);
	getChildren().add(initializeLabel());
	initializeStyle();
	initializeVertexEventHandlers(selectedTool, graph, editorSettings);
	var pulseTimeline = createPulseTimeline(1.1, Duration.millis(300));
	this.vertexValue.addFocusListener(() -> {
	    pulseTimeline.playFromStart();
	    this.requestFocus();
	});
    }

    protected Node initializeVertexRepresentation() {
	var diameter = 20.0;
	var circle = new Circle(diameter);
	vertexValue.shape().widthProperty().set(diameter);
	vertexValue.shape().heightProperty().set(diameter);
	circle.strokeTypeProperty().set(StrokeType.INSIDE);
	circle.getStyleClass().add("vertex-node");
	return circle;
    }

    protected Label initializeLabel() {
	var label = new Label(vertexKey.toString());
	label.getStyleClass().add("outline");
	return label;
    }

    private void initializeStyle() {
	bindTranslatePropertiesToViewmodel();
	bindSizePropertiesToViewmodel();
	addCursorHoverEffect();
    }

    private void bindTranslatePropertiesToViewmodel() {
	var vertexXProperty = vertexValue.position().getXProperty();
	var vertexYProperty = vertexValue.position().getYProperty();
	translateXProperty().bind(vertexXProperty.subtract(widthProperty().divide(2)));
	translateYProperty().bind(vertexYProperty.subtract(heightProperty().divide(2)));
    }

    private void bindSizePropertiesToViewmodel() {
	vertexValue.shape().scaleXProperty().set(scaleXProperty().get());
	vertexValue.shape().scaleYProperty().set(scaleYProperty().get());
	scaleXProperty().bind(vertexValue.shape().scaleXProperty());
	scaleYProperty().bind(vertexValue.shape().scaleYProperty());
    }
    
    private void addCursorHoverEffect() {
	setCursor(Cursor.HAND);
	var hoverEnterAnimation = createScaleTimeline(1, 1.1, Duration.millis(100));
	var hoverExitAnimation = createScaleTimeline(1.1, 1, Duration.millis(100));
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
	    hoverEnterAnimation.play();
	    graphic.getStyleClass().add("stroke-hover");
	});
	addEventHandler(MouseEvent.MOUSE_EXITED,  event -> {
	    hoverExitAnimation.play();
	    graphic.getStyleClass().remove("stroke-hover");
	});
    }

    private Timeline createScaleTimeline(double scaleBegin, double scaleEnd, Duration timelineTime) {
        var scale1x = new KeyValue(vertexValue.shape().scaleXProperty(), scaleBegin, Interpolator.EASE_BOTH);
        var scale1y = new KeyValue(vertexValue.shape().scaleYProperty(), scaleBegin, Interpolator.EASE_BOTH);
        var scale2x = new KeyValue(vertexValue.shape().scaleXProperty(), scaleEnd, Interpolator.EASE_BOTH);
        var scale2y = new KeyValue(vertexValue.shape().scaleYProperty(), scaleEnd, Interpolator.EASE_BOTH);
        var kx1 = new KeyFrame(Duration.millis(0), scale1x);
        var ky1 = new KeyFrame(Duration.millis(0), scale1y);
        var kx2 = new KeyFrame(timelineTime, scale2x);
        var ky2 = new KeyFrame(timelineTime, scale2y);
	return new Timeline(kx1, ky1, kx2, ky2);
    }

    private void initializeVertexEventHandlers(ObjectProperty<ITool> selectedTool, ViewModelGraph graph, ViewModelEditorSettings editorSettings) {
	addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onVertexMouseEvent(new VertexMouseEvent(e, vertexKey, vertexValue, viewportAffine, graph, editorSettings)));
	vertexValue.getIsSelected().addListener((e,o,n) -> {
	    if(n)
		graphic.getStyleClass().add("stroke-selected");
	    else
		graphic.getStyleClass().remove("stroke-selected");
	});
    }

    private Timeline createPulseTimeline(double intensity, Duration timelineTime) {
        var scale1x = new KeyValue(vertexValue.shape().scaleXProperty(), 1, Interpolator.EASE_BOTH);
        var scale2x = new KeyValue(vertexValue.shape().scaleXProperty(), 1 * intensity, Interpolator.EASE_BOTH);
        var scale3x = new KeyValue(vertexValue.shape().scaleXProperty(), 1, Interpolator.EASE_BOTH);
        var scale1y = new KeyValue(vertexValue.shape().scaleYProperty(), 1, Interpolator.EASE_BOTH);
        var scale2y = new KeyValue(vertexValue.shape().scaleYProperty(), 1 * intensity, Interpolator.EASE_BOTH);
        var scale3y = new KeyValue(vertexValue.shape().scaleYProperty(), 1, Interpolator.EASE_BOTH);
        var kx1 = new KeyFrame(Duration.millis(0), scale1x);
        var kx2 = new KeyFrame(timelineTime.multiply(0.5), scale2x);
        var kx3 = new KeyFrame(timelineTime, scale3x);
        var ky1 = new KeyFrame(Duration.millis(0), scale1y);
        var ky2 = new KeyFrame(timelineTime.multiply(0.5), scale2y);
        var ky3 = new KeyFrame(timelineTime, scale3y);
	return new Timeline(kx1, ky1, kx2, ky2, kx3, ky3);
    }
}

