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
     * private constructor. This class is only meant to be used in a static manner
     */
    private BindingsUtil() {

    }

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

    /**
     * Create a new {@link DoubleBinding} for the x-value on the edge of the provided shape and target position in a rectangular manner
     * @param sourcePosition The source position
     * @param targetPosition The target position. This will be the binding position
     * @param shape The shape dimensions to use
     * @return The new binding
     */
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

    /**
     * Create a new {@link DoubleBinding} for the x-value on the edge of the provided shape and target position in an oval manner
     * @param sourcePosition The source position
     * @param targetPosition The target position. This will be the binding position
     * @param shape The shape dimensions to use
     * @return The new binding
     */
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
     * @param shape The shape dimensions to use
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

    /**
     * Create a new {@link DoubleBinding} for the y-value on the edge of the provided shape and target position in a rectangular manner
     * @param sourcePosition The source position
     * @param targetPosition The target position. This will be the binding position
     * @param shape The shape dimensions to use
     * @return The new binding
     */
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

    /**
     * Create a new {@link DoubleBinding} for the y-value on the edge of the provided shape and target position in an oval manner
     * @param sourcePosition The source position
     * @param targetPosition The target position. This will be the binding position
     * @param shape The shape dimensions to use
     * @return The new binding
     */
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

    /**
     * Create a new {@link DoubleBinding} for a line that is bound to the end of a line
     * This is useful for "sticking" something to the end of a line and have it follow the rotation
     * @param line The line to reference
     * @return The new binding
     */
    public static DoubleBinding createRotationAtLineEndBinding(Line line) {
	return Bindings.createDoubleBinding(() -> 
		    (Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX()) * 180 / Math.PI),
		    line.endYProperty(), line.endXProperty(), line.startXProperty(), line.startYProperty());
    }

    /**
     * Create a new {@link DoubleBinding} for binding the provided dependency to a scaled, rotated and translated offset (X-values)
     * @param dependency The double to offset
     * @param offset The offset to use
     * @return A new binding that offsets dependency in a scaled and rotated manner by the translation of the provided offset
     */
    public static DoubleBinding createAffineOffsetXBinding(DoubleProperty dependency, Affine offset) {
	return dependency.subtract(offset.getTx()).divide(offset.getMxx());
    }

    /**
     * Create a new {@link DoubleBinding} for binding the provided dependency to a scaled, rotated and translated offset (Y-values)
     * @param dependency The double to offset
     * @param offset The offset to use
     * @return A new binding that offsets dependency in a scaled and rotated manner by the translation of the provided offset
     */
    public static DoubleBinding createAffineOffsetYBinding(DoubleProperty dependency, Affine offset) {
	return dependency.subtract(offset.getTy()).divide(offset.getMyy());
    }

    /**
     * Create a new {@link IntegerBinding} that maps a provided {@link StringProperty} using {@link Integer#valueOf}
     * Note that the binding itself may throw a {@link NumberFormatException} if the string dependency has a non-int value
     * @param dependency The string property to interpret
     * @return The new binding
     */
    public static IntegerBinding createStringInterpretedIntegerBinding(StringProperty dependency) {
	return Bindings.createIntegerBinding(() -> Integer.valueOf(dependency.get()), dependency);
    }

    /**
     * Create a new {@link DoubleBinding} that maps a provided {@link StringProperty} using {@link Double#valueOf}
     * Note that the binding itself may throw a {@link NumberFormatException} if the string dependency has a non-int value
     * @param dependency The string property to interpret
     * @return The new binding
     */
    public static DoubleBinding createStringInterpretedDoubleBinding(StringProperty dependency) {
	return Bindings.createDoubleBinding(() -> Double.valueOf(dependency.get()), dependency);
    }

    /**
     * Create a new {@link StringBinding} that maps a T-typed {@link Property} to a string representation using the toString method
     * @param <T> The type of the wrapped value
     * @param dependency The property to stringify
     * @return The new binding
     */
    public static <T> StringBinding createToStringBinding(Property<T> dependency) {
	return createToStringBinding("", dependency, "");
    }

    /**
     * Create a new {@link StringBinding} that maps a T-typed {@link Property} to a string representation using the toString method.
     * with a constant prefix prepended to the string value
     * @param <T> The type of the wrapped value
     * @param prefix The prefix to prepend
     * @param dependency The property to stringify
     * @return The new binding
     */
    public static <T> StringBinding createToStringBinding(String prefix, Property<T> dependency) {
	return createToStringBinding(prefix, dependency, "");
    }

    /**
     * Create a new {@link StringBinding} that maps a T-typed {@link Property} to a string representation using the toString method.
     * with a constant prefix and postfix prepended and appended respectively to the string value
     * @param <T> The type of the wrapped value
     * @param prefix The prefix to prepend
     * @param dependency The property to stringify
     * @param postfix The postfix to prepend
     * @return The new binding
     */
    public static <T> StringBinding createToStringBinding(String prefix, Property<T> dependency, String postfix) {
	return Bindings.createStringBinding(() -> prefix + dependency.getValue().toString() + postfix, dependency);
    }

    /**
     * Create a new {@link DoubleBinding} that is bound to somewhere along the line by some scalar offset (X-values)
     * @param line The line to offset along
     * @param scalar The offset amount ranging from [0..1], lower or higher will extend the point past the line
     * @return The new binding
     */
    public static DoubleBinding createLineOffsetXBinding(Line line, DoubleProperty scalar) {
        return Bindings.createDoubleBinding(() -> {
            var dirX = line.endXProperty().get() - line.startXProperty().get();
            var dirY = line.endYProperty().get() - line.startYProperty().get();
            var len = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
            var nDirX = dirX / len;
            return line.startXProperty().get() + (nDirX * (scalar.get() * len));
        }, line.startXProperty(), line.startYProperty(), line.endXProperty(), line.endYProperty(), scalar);
    }

    /**
     * Create a new {@link DoubleBinding} that is bound to somewhere along the line by some scalar offset (Y-values)
     * @param line The line to offset along
     * @param scalar The offset amount ranging from [0..1], lower or higher will extend the point past the line
     * @return The new binding
     */
    public static DoubleBinding createLineOffsetYBinding(Line line, DoubleProperty scalar) {
        return Bindings.createDoubleBinding(() -> {
            var dirX = line.endXProperty().get() - line.startXProperty().get();
            var dirY = line.endYProperty().get() - line.startYProperty().get();
            var len = Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2));
            var nDirY = dirY / len;
            return line.startYProperty().get() + (nDirY * (scalar.get() * len));
        }, line.startXProperty(), line.startYProperty(), line.endXProperty(), line.endYProperty(), scalar);
    }
}
