package dk.gtz.graphedit.view.util;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;

/**
 * General static utilities for various types bindings ({@link javafx.beans.binding})
 */
public class BindingsUtil {
    /**
     * Create a new {@link DoubleBinding} for the x-value on the edge of the provided shape and target position
     * @see createRectangularXBinding
     * @see createOvalXBinding
     * @param sourcePosition The source position
     * @param targetPosition The target position. This will be the binding position
     * @param shape The type of shape to bind as
     * @return The new binding
     * @throws RuntimeException if the provided {@code ViewModelVertexShape} value is not supported
     */
    public static DoubleBinding createShapedXBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	if(shape.shapeType().get().equals(ViewModelShapeType.RECTANGLE))
	    return createRectangularXBinding(sourcePosition, targetPosition, shape);
	if(shape.shapeType().get().equals(ViewModelShapeType.OVAL))
	    return createOvalXBinding(sourcePosition, targetPosition, shape);
	throw new RuntimeException("no binding implemented for shape %s".formatted(shape.shapeType().getName()));
    }

    public static DoubleBinding createRectangularXBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	return Bindings.createDoubleBinding(() -> {
	    var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
	    var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
	    var intersection = ShapeUtil.rectangleIntersect(shape, diffX, diffY);
	    return targetPosition.getXProperty().get() - intersection.getX();
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(),
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.widthProperty(), shape.scaleXProperty());
    }

    public static DoubleBinding createOvalXBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	return Bindings.createDoubleBinding(() -> {
	    var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
	    var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
	    var angle = Math.atan2(diffY, diffX);
	    var opposite = Math.cos(angle);
	    var scaled = opposite * shape.widthProperty().get() * shape.scaleXProperty().get();
	    return targetPosition.getXProperty().get() - scaled;
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(),
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.widthProperty(), shape.scaleXProperty());
    }

    /**
     * Create a new {@link DoubleBinding} for the y-value on the edge of the provided shape and target position
     * @see createRectangularYBinding
     * @see createOvalYBinding
     * @param sourcePosition The source position
     * @param targetPosition The target position. This will be the binding position
     * @param shape The type of shape to bind as
     * @return The new binding
     * @throws RuntimeException if the provided {@code ViewModelVertexShape} value is not supported
     */
    public static DoubleBinding createShapedYBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	if(shape.shapeType().get().equals(ViewModelShapeType.RECTANGLE))
	    return createRectangularYBinding(sourcePosition, targetPosition, shape);
	if(shape.shapeType().get().equals(ViewModelShapeType.OVAL))
	    return createOvalYBinding(sourcePosition, targetPosition, shape);
	throw new RuntimeException("no binding implemented for shape %s".formatted(shape.shapeType().getName()));
    }

    public static DoubleBinding createRectangularYBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	return Bindings.createDoubleBinding(() -> {
	    var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
	    var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
	    var intersection = ShapeUtil.rectangleIntersect(shape, diffX, diffY);
	    return targetPosition.getYProperty().get() - intersection.getY();
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(),
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.heightProperty(), shape.scaleYProperty());
    }

    public static DoubleBinding createOvalYBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	return Bindings.createDoubleBinding(() -> {
	    var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
	    var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
	    var angle = Math.atan2(diffY, diffX);
	    var opposite = Math.sin(angle);
	    var scaled = opposite * shape.heightProperty().get() * shape.scaleYProperty().get();
	    return targetPosition.getYProperty().get() - scaled;
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(),
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.heightProperty(), shape.scaleYProperty());
    }

    public static DoubleBinding createRotationAtLineEndBinding(Line line) {
	return Bindings.createDoubleBinding(() -> 
		    (Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX()) * 180 / Math.PI),
		    line.endYProperty(), line.endXProperty(), line.startXProperty(), line.startYProperty());
    }

    public static DoubleBinding createAffineOffsetXBinding(DoubleProperty dependency, Affine offset) {
	return Bindings.createDoubleBinding(() -> dependency.subtract(offset.getTx()).divide(offset.getMxx()).get(), dependency);
    }

    public static DoubleBinding createAffineOffsetYBinding(DoubleProperty dependency, Affine offset) {
	return Bindings.createDoubleBinding(() -> dependency.subtract(offset.getTy()).divide(offset.getMyy()).get(), dependency);
    }
}

