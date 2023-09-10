package dk.gtz.graphedit.viewmodel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A shape construct used to define the dimensions and scale of a specific type of shape
 */
public record ViewModelVertexShape(DoubleProperty scaleXProperty, DoubleProperty scaleYProperty, DoubleProperty widthProperty, DoubleProperty heightProperty, ObjectProperty<ViewModelShapeType> shapeType) {
    public ViewModelVertexShape(ViewModelShapeType shape) {
        this(new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleObjectProperty<>(shape));
    }

    public ViewModelVertexShape(double scaleX, double scaleY, double width, double height, ViewModelShapeType type) {
        this(new SimpleDoubleProperty(scaleX), new SimpleDoubleProperty(scaleY), new SimpleDoubleProperty(width), new SimpleDoubleProperty(height), new SimpleObjectProperty<>(type));
    }

    public ViewModelVertexShape() {
        this(new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleObjectProperty<>(ViewModelShapeType.RECTANGLE));
    }
}

