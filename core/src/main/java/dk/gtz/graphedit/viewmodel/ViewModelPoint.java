package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelPoint;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ViewModelPoint {
    private final DoubleProperty x;
    private final DoubleProperty y;

    public ViewModelPoint(ModelPoint point) {
        this(point.x(), point.y());
    }

    public ViewModelPoint(double x, double y) {
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
    }

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty getXProperty() {
        return x;
    }

    public DoubleProperty getYProperty() {
        return y;
    }
}

