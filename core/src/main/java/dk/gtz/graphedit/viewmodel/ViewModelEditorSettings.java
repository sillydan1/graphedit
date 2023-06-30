package dk.gtz.graphedit.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public record ViewModelEditorSettings(
        DoubleProperty gridSizeX,
        DoubleProperty gridSizeY,
        BooleanProperty gridSnap) {
    public ViewModelEditorSettings(double gridSizeX, double gridSizeY, boolean gridSnap) {
        this(
                new SimpleDoubleProperty(gridSizeX),
                new SimpleDoubleProperty(gridSizeY),
                new SimpleBooleanProperty(gridSnap)
            );
    }
}


