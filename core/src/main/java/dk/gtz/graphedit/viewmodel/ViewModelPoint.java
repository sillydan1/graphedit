package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelPoint;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * View model representation of a 2D point
 */
public class ViewModelPoint {
    private final DoubleProperty x;
    private final DoubleProperty y;

    /**
     * Constructs a new view model point based on the provided model point
     * @param point the model point to base on
     */
    public ViewModelPoint(ModelPoint point) {
        this(point.x(), point.y());
    }

    /**
     * Constructs a new view model point with the provided x and y coordinates
     * @param x the x-coordinate value
     * @param y the y-coordinate value
     */
    public ViewModelPoint(double x, double y) {
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
    }

    /**
     * Constructs a new view model point with the provided x and y coordinate properties
     * @param x the x-coordinate property
     * @param y the y-coordinate property
     */
    public ViewModelPoint(DoubleProperty x, DoubleProperty y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new view model point with the provided x and y coordinate property bindings
     * @param x the x-coordinate property binding
     * @param y the y-coordinate property binding
     */
    public ViewModelPoint(DoubleBinding x, DoubleBinding y) {
        this(x.get(), y.get());
        this.x.bind(x);
        this.y.bind(y);
    }

    /**
     * Get the x-coordinate value of the view model point
     * @return the underlying x-coordinate value
     */
    public double getX() {
        return x.get();
    }

    /**
     * Get the y-coordinate value of the view model point
     * @return the underlying y-coordinate value
     */
    public double getY() {
        return y.get();
    }

    /**
     * Get the x-coordinate property of the view model point.
     * Useful for attaching eventlisteners
     * @return the x-coordinate property
     */
    public DoubleProperty getXProperty() {
        return x;
    }

    /**
     * Get the y-coordinate property of the view model point.
     * Useful for attaching eventlisteners
     * @return the y-coordinate property
     */
    public DoubleProperty getYProperty() {
        return y;
    }

    /**
     * Constructs a new model point instance based on the current view model values
     * @return a new model point instance
     */
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

    /**
     * Vector-subtraction function for view model points.
     * Note: This does not modify the current values.
     * @param other the values to subtract
     * @return a new instance with the new subtracted coordinate values
     */
    public ViewModelPoint subtract(ViewModelPoint other) {
        return new ViewModelPoint(getX() - other.getX(), getY() - other.getY());
    }

    /**
     * Vector-addition function for view model points.
     * Note: This does not modify the current values.
     * @param other the values to add
     * @return a new instance with the new added coordinate values
     */
    public ViewModelPoint add(ViewModelPoint other) {
        return new ViewModelPoint(getX() + other.getX(), getY() + other.getY());
    }

    /**
     * Vector-multiplication function for view model points.
     * Note: This does not modify the current values.
     * @param scalar the value to scale the values with
     * @return a new instance with the new coordinate values
     */
    public ViewModelPoint scale(float scalar) {
        return new ViewModelPoint(getX() * scalar, getY() * scalar);
    }
}

