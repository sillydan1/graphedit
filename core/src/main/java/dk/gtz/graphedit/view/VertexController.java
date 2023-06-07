package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

public class VertexController extends StackPane {
    private final UUID vertexKey;
    private final ViewModelVertex vertexValue;
    private final Affine viewportAffine;

    public VertexController(UUID vertexKey, ViewModelVertex vertex, Affine viewportAffine) {
	this.vertexKey = vertexKey;
	this.vertexValue = vertex;
	this.viewportAffine = viewportAffine;
	initialize();
    }

    private void initialize() {
	getChildren().add(initializeVertexRepresentation());
	getChildren().add(initializeLabel());
	initializeStyle();
	initializeInteractionEvents();
    }

    private Circle initializeVertexRepresentation() {
	var circle = new Circle(20.0);
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
	addCursorHoverEffect();
    }

    private void bindTranslatePropertiesToViewmodel() {
	var vertexXProperty = vertexValue.position().getXProperty();
	var vertexYProperty = vertexValue.position().getYProperty();
	translateXProperty().bind(Bindings.createDoubleBinding(() -> vertexXProperty.get() - (widthProperty().get()  / 2), vertexXProperty, widthProperty()));
	translateYProperty().bind(Bindings.createDoubleBinding(() -> vertexYProperty.get() - (heightProperty().get() / 2), vertexYProperty, heightProperty()));
    }
    
    private void addCursorHoverEffect() {
	setCursor(Cursor.HAND);
	var hoverEnterAnimation = createScaleTimeline(1, 1.1, Duration.millis(100));
	var hoverExitAnimation = createScaleTimeline(1.1, 1, Duration.millis(100));
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> hoverEnterAnimation.play());
	addEventHandler(MouseEvent.MOUSE_EXITED,  event -> hoverExitAnimation.play());
    }

    private Timeline createScaleTimeline(double scaleBegin, double scaleEnd, Duration timelineTime) {
        var scale1x = new KeyValue(scaleXProperty(), scaleBegin, Interpolator.EASE_BOTH);
        var scale1y = new KeyValue(scaleYProperty(), scaleBegin, Interpolator.EASE_BOTH);
        var scale2x = new KeyValue(scaleXProperty(), scaleEnd, Interpolator.EASE_BOTH);
        var scale2y = new KeyValue(scaleYProperty(), scaleEnd, Interpolator.EASE_BOTH);
        var kx1 = new KeyFrame(Duration.millis(0), scale1x);
        var ky1 = new KeyFrame(Duration.millis(0), scale1y);
        var kx2 = new KeyFrame(timelineTime, scale2x);
        var ky2 = new KeyFrame(timelineTime, scale2y);
	return new Timeline(kx1, ky1, kx2, ky2);
    }

    private void initializeInteractionEvents() {
	DragUtil.makeDraggable(this, vertexValue.position(), viewportAffine);
    }
}

