package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelPoint;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * View model representation of a 2D point
 */
public class ViewModelPoint implements Property<ViewModelPoint> {
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

    /**
     * Calculate the dot product between this and another point (as if they are vectors).
     * @param other The other point to dot
     * @return The dot product of this and the other point as a vector 
     */
    public double dot(ViewModelPoint other) {
	return (getX() * other.getX()) + (getY() * other.getY());
    }

    public ViewModelPoint copy() {
	return new ViewModelPoint(getX(), getY());
    }

    public ViewModelPoint multiply(ViewModelPoint other) {
	return new ViewModelPoint(getX() * other.getX(), getY() * other.getY());
    }

    public double angle(ViewModelPoint other) {
	return Math.toDegrees(Math.acos(dot(other) / (distance() * other.distance())));
    }

    public ViewModelPoint normalized() {
	var magnitude = distance();
	return new ViewModelPoint(getX() / magnitude, getY() / magnitude);
    }

    public double distance() {
	return Math.sqrt((getX() * getX()) + (getY() * getY()));
    }

    @Override
    public Object getBean() {
	return null;
    }

    @Override
    public String getName() {
	return "";
    }

    @Override
    public void addListener(ChangeListener<? super ViewModelPoint> listener) {
	x.addListener((e,o,n) -> listener.changed(this, this, this));
	y.addListener((e,o,n) -> listener.changed(this, this, this));
    }

    @Override
    public void removeListener(ChangeListener<? super ViewModelPoint> listener) {
	x.removeListener((e,o,n) -> listener.changed(this, this, this));
	y.removeListener((e,o,n) -> listener.changed(this, this, this));
    }

    @Override
    public ViewModelPoint getValue() {
	return this;
    }

    @Override
    public void addListener(InvalidationListener listener) {
	x.addListener(listener);
	y.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
	x.removeListener(listener);
	y.removeListener(listener);
    }

    @Override
    public void setValue(ViewModelPoint value) {
	x.set(value.getX());
	y.set(value.getY());
    }

    @Override
    public void bind(ObservableValue<? extends ViewModelPoint> observable) {
	x.bind(observable.getValue().getYProperty());
	y.bind(observable.getValue().getYProperty());
    }

    @Override
    public void unbind() {
	throw new UnsupportedOperationException("Unimplemented method 'unbind'");
    }

    @Override
    public boolean isBound() {
	return x.isBound() || y.isBound();
    }

    @Override
    public void bindBidirectional(Property<ViewModelPoint> other) {
	x.bindBidirectional(other.getValue().x);
	y.bindBidirectional(other.getValue().y);
    }

    @Override
    public void unbindBidirectional(Property<ViewModelPoint> other) {
	x.unbindBidirectional(other.getValue().x);
	y.unbindBidirectional(other.getValue().y);
    }

    @Override
    public boolean equals(Object other) {
	if(other == null)
	    return false;
	if(!(other instanceof ViewModelPoint vother))
	    return false;
	return x.get() == vother.x.get() && y.get() == vother.y.get();
    }

    @Override
    public int hashCode() {
	return x.getValue().hashCode() ^ y.getValue().hashCode();
    }
}
