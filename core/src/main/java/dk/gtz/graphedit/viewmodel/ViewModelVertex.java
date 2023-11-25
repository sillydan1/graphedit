package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * View model representation of a {@link ModelVertex}.
 * A vertex is the most basic part of a graph. It can be connected with other vertices via {@link ModelEdge}s.
 */
public class ViewModelVertex implements IInspectable, IHoverable, ISelectable, IFocusable, Property<ViewModelVertex> {
    private Logger logger = LoggerFactory.getLogger(ViewModelVertex.class);
    private final UUID uuid;
    private final ViewModelPoint position;
    private final ViewModelVertexShape shape;
    private final BooleanProperty isSelected;
    private final List<Runnable> focusEventHandlers;
    private final ObjectProperty<Node> hoverElement;

    /**
     * Constructs a new view model vertex based on a position and a shape
     * @param position the point at which the vertex is located
     * @param shape the shape at which edges should follow
     */
    public ViewModelVertex(UUID uuid, ViewModelPoint position, ViewModelVertexShape shape) {
	this.uuid = uuid;
	this.position = position;
	this.shape = shape;
	this.isSelected = new SimpleBooleanProperty(false);
	this.focusEventHandlers = new ArrayList<>();
	this.hoverElement = new SimpleObjectProperty<>();
    }

    /**
     * Constructs a new view model vertex based on a model vertex and a shape
     * @param vertex the model vertex to base on
     * @param shape the shape at which edges should follow
     */
    public ViewModelVertex(UUID uuid, ModelVertex vertex, ViewModelVertexShape shape) {
	this(uuid, new ViewModelPoint(vertex.position), shape);
    }

    /**
     * Constructs a new view model vertex based on a model vertex
     * @param vertex the model vertex to base on
     */
    public ViewModelVertex(UUID uuid, ModelVertex vertex) {
	this(uuid, vertex, new ViewModelVertexShape());
    }

    /**
     * Constructs a new model vertex instance based on the current view model values
     * @return a new model vertex instance
     */
    public ModelVertex toModel() {
	return new ModelVertex(position.toModel());
    }

    /**
     * Get the position of the vertex
     * @return a view model point
     */
    public ViewModelPoint position() {
	return position;
    }

    /**
     * Get the shape of the vertex
     * @return a view model shape
     */
    public ViewModelVertexShape shape() {
	return shape;
    }

    /**
     * Get the id of the vertex
     * @return the unique identifier of the vertex
     */
    public UUID id() {
	return uuid;
    }

    @Override
    public BooleanProperty getIsSelected() {
	return isSelected;
    }

    @Override
    public void select() {
	getIsSelected().set(true);
    }

    @Override
    public void deselect() {
	getIsSelected().set(false);
    }

    @Override
    public void addFocusListener(Runnable focusEventHandler) {
	focusEventHandlers.add(focusEventHandler);
    }

    @Override
    public void focus() {
	focusEventHandlers.forEach(Runnable::run);
    }

    /**
     * {@inheritDoc}
     * @return An unmodifiable list of inspectable objects
     * */
    @Override
    public List<InspectableProperty> getInspectableObjects() {
	return List.of(
		new InspectableProperty("Position X", position.getXProperty()),
		new InspectableProperty("Position Y", position.getYProperty()),
		new InspectableProperty("Shape Width", shape.widthProperty()),
		new InspectableProperty("Shape Height", shape.heightProperty()));
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
    public void addListener(ChangeListener<? super ViewModelVertex> listener) {
	position.addListener((e,o,n) -> listener.changed(this,this,this));
    }

    @Override
    public void removeListener(ChangeListener<? super ViewModelVertex> listener) {
	position.removeListener((e,o,n) -> listener.changed(this,this,this));
    }

    @Override
    public ViewModelVertex getValue() {
	return this;
    }

    @Override
    public void addListener(InvalidationListener listener) {
	position.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
	position.removeListener(listener);
    }

    @Override
    public void setValue(ViewModelVertex value) {
	position.setValue(value.position);
    }

    @Override
    public void bind(ObservableValue<? extends ViewModelVertex> observable) {
	position.bind(observable.getValue().position());
    }

    @Override
    public void unbind() {
	position.unbind();
    }

    @Override
    public boolean isBound() {
	return position.isBound();
    }

    @Override
    public void bindBidirectional(Property<ViewModelVertex> other) {
	position.bindBidirectional(other.getValue().position());
    }

    @Override
    public void unbindBidirectional(Property<ViewModelVertex> other) {
	position.unbindBidirectional(other.getValue().position());
    }

    @Override
    public void hover(Node node) {
	hoverElement.set(node);
    }

    @Override
    public void addHoverListener(ChangeListener<Node> consumer) {
	hoverElement.addListener(consumer);
    }

    @Override
    public boolean isHovering() {
	return hoverElement.isNotNull().get();
    }

    @Override
    public void unhover() {
	hoverElement.set(null);
    }

    @Override
    public boolean equals(Object other) {
	if(other == null)
	    return false;
	if(!(other instanceof ViewModelVertex vother))
	    return false;
	if(!uuid.equals(vother.uuid))
	    return false;
	if(!position.equals(vother.position))
	    return false;
	return true;
    }

    @Override
    public int hashCode() {
	return position.hashCode() ^ uuid.hashCode();
    }
}
