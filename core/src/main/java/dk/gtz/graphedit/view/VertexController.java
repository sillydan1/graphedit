package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.beans.binding.Bindings;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;

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
	setTranslateX(vertexValue.position().getX());
	setTranslateY(vertexValue.position().getY());

	var vertexXProperty = vertexValue.position().getXProperty();
	var vertexYProperty = vertexValue.position().getYProperty();
	translateXProperty().bind(Bindings.createDoubleBinding(() -> vertexXProperty.get() - (widthProperty().get() / 2), vertexXProperty, widthProperty()));
	translateYProperty().bind(Bindings.createDoubleBinding(() -> vertexYProperty.get() - (heightProperty().get() / 2), vertexYProperty, heightProperty()));
	setCursor(Cursor.HAND);
	// TODO: on hover scale with an animation
    }

    private void initializeInteractionEvents() {
	DragUtil.makeDraggable(this, vertexValue.position(), viewportAffine);
    }
}

