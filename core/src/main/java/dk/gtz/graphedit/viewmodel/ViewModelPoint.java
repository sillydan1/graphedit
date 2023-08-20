package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelPoint;
import javafx.beans.binding.DoubleBinding;
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

    public ViewModelPoint(DoubleProperty x, DoubleProperty y) {
        this.x = x;
        this.y = y;
    }

    public ViewModelPoint(DoubleBinding x, DoubleBinding y) {
        this(x.get(), y.get());
        this.x.bind(x);
        this.y.bind(y);
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

    public ModelPoint toModel() {
        return new ModelPoint(getX(), getY());
    }

    /**
     * Snap the position to the provided grid settings
     * @param settings the view settings containing grid settings to snap to
     */
    public void snapToGrid(ViewModelEditorSettings settings) {
        getXProperty().set(getX() - (getX() % settings.gridSizeX().get()));
        getYProperty().set(getY() - (getY() % settings.gridSizeY().get()));
    }
}

