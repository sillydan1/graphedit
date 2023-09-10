package dk.gtz.graphedit.view.util;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;

/**
 * General utilities for handling geometric shapes
 */
public class ShapeUtil {
    /**
     * Will calculate the intersection point on a reactangle shape given a ray (dx,dy)
     * @see <a href="https://math.stackexchange.com/questions/2738250/intersection-of-ray-starting-inside-square-with-that-square/2738727#2738727">inspired stackoverflow answer</a>
     * @param rect rectangle dimensions
     * @param dx ray x direction
     * @param dy ray y direction
     * @throws RuntimeException if the provided shape is not marked as a rectangle
     * @return The intersection point on the rectangle. Note: (0,0) if the both dx and dy are zero
     */
    public static ViewModelPoint rectangleIntersect(ViewModelVertexShape rect, double dx, double dy) {
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
}

