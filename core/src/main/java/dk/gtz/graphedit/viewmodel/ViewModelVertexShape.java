package dk.gtz.graphedit.viewmodel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A shape construct used to define the dimensions and scale of a specific type of shape
 * @param scaleXProperty The X scale-factor of the shape
 * @param scaleYProperty The Y scale-factor of the shape
 * @param widthProperty The width of the shape
 * @param heightProperty The height of the shape
 * @param shapeType The shape of the vertex
 */
public record ViewModelVertexShape(
        DoubleProperty scaleXProperty,
        DoubleProperty scaleYProperty,
        DoubleProperty widthProperty,
        DoubleProperty heightProperty,
        ObjectProperty<ViewModelShapeType> shapeType) {

    /**
     * Constructs a new rectange vertex shape
     */
    public ViewModelVertexShape() {
        this(ViewModelShapeType.RECTANGLE);
    }

    /**
     * Constructs a new vertex shape viewmodel with a specified shape
     * @param shape The shape of the vertex
     */
    public ViewModelVertexShape(ViewModelShapeType shape) {
        this(1,1,0,0,shape);
    }

    /**
     * Constructs a new vertex shape viewmodel with specified dimensions
     * @param scaleX The X scale-factor of the shape
     * @param scaleY Property The Y scale-factor of the shape
     * @param width Property The width of the shape
     * @param height Property The height of the shape
     * @param type The shape of the vertex
     */
    public ViewModelVertexShape(
            double scaleX,
            double scaleY,
            double width,
            double height,
            ViewModelShapeType type) {
        this(
                new SimpleDoubleProperty(scaleX),
                new SimpleDoubleProperty(scaleY),
                new SimpleDoubleProperty(width),
                new SimpleDoubleProperty(height),
                new SimpleObjectProperty<>(type)
            );
    }
}
