package dk.gtz.graphedit.view;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;


/**
 * A point and a shape.
 * @param point the point
 * @param shape the shape
 */
public record PointShape(ViewModelPoint point, ViewModelVertexShape shape) {}
