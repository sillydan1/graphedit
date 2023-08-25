package dk.gtz.graphedit.view.util;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;

public class BindingsUtil {
    // TODO: This is a MathUtil or ShapeUtil func. Not a BindingsUtil
    /**
     * Will calculate the intersection point on a reactangle shape given a ray (dx,dy)
     * @see <a href="https://math.stackexchange.com/questions/2738250/intersection-of-ray-starting-inside-square-with-that-square/2738727#2738727">inspired stackoverflow answer</a>
     * @param rect rectangle dimensions
     * @param dx ray x direction
     * @param dy ray y direction
     * @throws RuntimeException if the provided shape is not marked as a rectangle
     * @return The intersection point on the rectangle
     * @implNote Will return (0,0) if the both dx and dy are zero
     */
    private static ViewModelPoint rectangleIntersect(ViewModelVertexShape rect, double dx, double dy) {
	if(!rect.shapeType().get().equals(ViewModelShapeType.RECTANGLE))
	    throw new RuntimeException("Shape is not a rectangle");
	if(dx == 0 && dy == 0)
	    return new ViewModelPoint(0, 0);
	var halfWidth = (rect.widthProperty().get() * rect.scaleXProperty().get()) / 2;
	var halfHeight = (rect.heightProperty().get() * rect.scaleYProperty().get()) / 2;
	if(dx < 0)
	    halfWidth = -halfWidth;
	if(dy < 0)
	    halfHeight = -halfHeight;
	var tx = halfWidth / dx;
	var ty = halfHeight / dy;
	if(tx < ty)
	    return new ViewModelPoint(tx * dx, tx * dy);
	if(tx > ty)
	    return new ViewModelPoint(ty * dx, ty * dy);
	return new ViewModelPoint(tx * dx, ty * dy);
    }

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
	    var intersection = rectangleIntersect(shape, diffX, diffY);
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
	    var intersection = rectangleIntersect(shape, diffX, diffY);
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

