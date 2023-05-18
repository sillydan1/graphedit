package dk.gtz.graphedit.viewmodel;

import java.awt.Point;

import dk.gtz.graphedit.model.ModelVertex;
import javafx.beans.property.SimpleObjectProperty;

public record ViewModelVertex(SimpleObjectProperty<Point> position) {
    public ViewModelVertex(ModelVertex vertex) {
        this(new SimpleObjectProperty<>(vertex.position()));
    }
}

