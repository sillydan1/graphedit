package dk.gtz.graphedit.util;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
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
	// TODO: refactor to reuse some of this duplicated code
	return Bindings.createDoubleBinding(() -> {
	    if(shape.shapeType().get().equals(ViewModelShapeType.RECTANGLE)) {
		var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
		var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
		var intersection = ShapeUtil.rectangleIntersect(shape, diffX, diffY);
		return targetPosition.getXProperty().get() - intersection.getX();
	    }
	    if(shape.shapeType().get().equals(ViewModelShapeType.OVAL)) {
		var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
		var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
		var angle = Math.atan2(diffY, diffX);
		var opposite = Math.cos(angle);
		var scaled = opposite * shape.widthProperty().get() * shape.scaleXProperty().get();
		return targetPosition.getXProperty().get() - scaled;
	    }
	    throw new RuntimeException("no binding implemented for shape %s".formatted(shape.shapeType().getName()));
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(),
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.widthProperty(), shape.scaleXProperty());
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
	// TODO: refactor to reuse some of this duplicated code
	return Bindings.createDoubleBinding(() -> {
	    if(shape.shapeType().get().equals(ViewModelShapeType.RECTANGLE)) {
		var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
		var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
		var intersection = ShapeUtil.rectangleIntersect(shape, diffX, diffY);
		return targetPosition.getYProperty().get() - intersection.getY();
	    }
	    if(shape.shapeType().get().equals(ViewModelShapeType.OVAL)) {
		var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
		var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
		var angle = Math.atan2(diffY, diffX);
		var opposite = Math.sin(angle);
		var scaled = opposite * shape.heightProperty().get() * shape.scaleYProperty().get();
		return targetPosition.getYProperty().get() - scaled;
	    }
	    throw new RuntimeException("no binding implemented for shape %s".formatted(shape.shapeType().getName()));
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(),
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.widthProperty(), shape.scaleXProperty());
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
	return dependency.subtract(offset.getTx()).divide(offset.getMxx());
    }

    public static DoubleBinding createAffineOffsetYBinding(DoubleProperty dependency, Affine offset) {
	return dependency.subtract(offset.getTy()).divide(offset.getMyy());
    }

    public static IntegerBinding createStringInterpretedIntegerBinding(StringProperty dependency) {
	return Bindings.createIntegerBinding(() -> Integer.valueOf(dependency.get()), dependency);
    }

    public static DoubleBinding createStringInterpretedDoubleBinding(StringProperty dependency) {
	return Bindings.createDoubleBinding(() -> Double.valueOf(dependency.get()), dependency);
    }

    public static <T> StringBinding createToStringBinding(Property<T> dependency) {
	return createToStringBinding("", dependency, "");
    }

    public static <T> StringBinding createToStringBinding(String prefix, Property<T> dependency) {
	return createToStringBinding(prefix, dependency, "");
    }

    public static <T> StringBinding createToStringBinding(String prefix, Property<T> dependency, String postfix) {
	return Bindings.createStringBinding(() -> prefix + dependency.getValue().toString() + postfix, dependency);
    }

    public static DoubleBinding getLineOffsetXBinding(DoubleProperty lineStartX, DoubleProperty lineStartY, DoubleProperty lineEndX, DoubleProperty lineEndY, DoubleProperty scalar) {
        return Bindings.createDoubleBinding(() -> {
            var dirX = lineEndX.get() - lineStartX.get();
            var dirY = lineEndY.get() - lineStartY.get();
            var len = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
            var nDirX = dirX / len;
            return lineStartX.get() + (nDirX * (scalar.get() * len));
        }, lineStartX, lineStartY, lineEndX, lineEndY, scalar);
    }

    public static DoubleBinding getLineOffsetYBinding(DoubleProperty lineStartX, DoubleProperty lineStartY, DoubleProperty lineEndX, DoubleProperty lineEndY, DoubleProperty scalar) {
        return Bindings.createDoubleBinding(() -> {
            var dirX = lineEndX.get() - lineStartX.get();
            var dirY = lineEndY.get() - lineStartY.get();
            var len = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
            var nDirY = dirY / len;
            return lineStartY.get() + (nDirY * (scalar.get() * len));
        }, lineStartX, lineStartY, lineEndX, lineEndY, scalar);
    }
}

