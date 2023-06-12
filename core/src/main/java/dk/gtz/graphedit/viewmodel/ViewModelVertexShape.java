package dk.gtz.graphedit.viewmodel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public record ViewModelVertexShape(DoubleProperty scaleXProperty, DoubleProperty scaleYProperty, DoubleProperty widthProperty, DoubleProperty heightProperty, ObjectProperty<ViewModelShapeType> shapeType) {
    public ViewModelVertexShape() {
        this(new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleDoubleProperty(), new SimpleObjectProperty<>(ViewModelShapeType.RECTANGLE));
    }
}

