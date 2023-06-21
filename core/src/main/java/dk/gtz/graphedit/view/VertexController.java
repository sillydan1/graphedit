package dk.gtz.graphedit.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

public class VertexController extends StackPane {
    private final Logger logger = LoggerFactory.getLogger(VertexController.class);
    private final UUID vertexKey;
    private final ViewModelVertex vertexValue;
    private final Affine viewportAffine;
    private Circle circle;

    public VertexController(UUID vertexKey, ViewModelVertex vertex, Affine viewportAffine, ViewModelGraph graph, ObjectProperty<ITool> selectedTool) {
	this.vertexKey = vertexKey;
	this.vertexValue = vertex;
	this.viewportAffine = viewportAffine;
	initialize(selectedTool, graph);
    }

    private void initialize(ObjectProperty<ITool> selectedTool, ViewModelGraph graph) {
	getChildren().add(initializeVertexRepresentation());
	getChildren().add(initializeLabel());
	initializeStyle();
	initializeVertexEventHandlers(selectedTool, graph);
    }

    private Circle initializeVertexRepresentation() {
	var diameter = 20.0;
	circle = new Circle(diameter);
	vertexValue.shape().widthProperty().set(diameter);
	vertexValue.shape().heightProperty().set(diameter);
	circle.strokeTypeProperty().set(StrokeType.INSIDE);
	circle.getStyleClass().add("vertex-node");
	return circle;
    }

    private Label initializeLabel() {
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
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> hoverEnterAnimation.play());
	addEventHandler(MouseEvent.MOUSE_EXITED,  event -> hoverExitAnimation.play());
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

    private void initializeVertexEventHandlers(ObjectProperty<ITool> selectedTool, ViewModelGraph graph) {
	addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onVertexMouseEvent(new VertexMouseEvent(e, vertexKey, vertexValue, viewportAffine, graph)));
	vertexValue.getIsSelected().addListener((e,o,n) -> {
	    if(n)
		circle.getStyleClass().add("vertex-node-selected");
	    else
		circle.getStyleClass().remove("vertex-node-selected");
	});
    }
}

