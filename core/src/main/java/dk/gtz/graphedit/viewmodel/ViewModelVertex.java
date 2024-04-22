package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;

/**
 * Viewmodel representation of a {@link ModelVertex}.
 * A vertex is the most basic part of a graph. It can be connected with other vertices via {@link ModelEdge}s.
 */
public class ViewModelVertex extends AutoProperty<ViewModelVertex> implements IInspectable, IHoverable, ISelectable, IFocusable {
    private final static Logger logger = LoggerFactory.getLogger(ViewModelVertex.class);

    private final UUID uuid;
    private final ViewModelVertexShape shape;
    private final BooleanProperty isSelected;
    private final List<Runnable> focusEventHandlers;
    private final ObjectProperty<Node> hoverElement;

    /**
     * The position of the vertex
     */
    @Autolisten
    public final ViewModelPoint position;

    /**
     * Constructs a new view model vertex based on a position and a shape
     * @param uuid The id of the vertex
     * @param position The point at which the vertex is located
     * @param shape The shape at which edges should follow
     */
    public ViewModelVertex(UUID uuid, ViewModelPoint position, ViewModelVertexShape shape) {
	super();
	loadFields(getClass(), this);
	this.uuid = uuid;
	this.position = position;
	this.shape = shape;
	this.isSelected = new SimpleBooleanProperty(false);
	this.focusEventHandlers = new ArrayList<>();
	this.hoverElement = new SimpleObjectProperty<>();
    }

    /**
     * Constructs a new view model vertex based on a model vertex and a shape
     * @param uuid The id of the vertex
     * @param vertex The model vertex to base on
     * @param shape The shape at which edges should follow
     */
    public ViewModelVertex(UUID uuid, ModelVertex vertex, ViewModelVertexShape shape) {
	this(uuid, new ViewModelPoint(vertex.position), shape);
    }

    /**
     * Constructs a new view model vertex based on a model vertex
     * @param uuid The id of the vertex
     * @param vertex The model vertex to base on
     */
    public ViewModelVertex(UUID uuid, ModelVertex vertex) {
	this(uuid, vertex, new ViewModelVertexShape());
    }

    /**
     * Constructs a new model vertex instance based on the current view model values
     * @return A new model vertex instance
     */
    public ModelVertex toModel() {
	return new ModelVertex(position.toModel());
    }

    /**
     * Get the position of the vertex
     * @return A view model point
     */
    public ViewModelPoint position() {
	return position;
    }

    /**
     * Get the shape of the vertex
     * @return A view model shape
     */
    public ViewModelVertexShape shape() {
	return shape;
    }

    /**
     * Get the id of the vertex
     * @return The unique identifier of the vertex
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
	return List.of();
    }

    @Override
    public String getName() {
	return "";
    }

    @Override
    public ViewModelVertex getValue() {
	return this;
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

    /**
     * Indicates whether or not the provided diff is significant in terms of the semantics.
     * If this returns true, then a change-event is triggered towards the language server (if available).
     * @param other The other vertex to compare with
     * @return True if the change is significant, false otherwise
     */
    public boolean isChangeSignificant(ViewModelVertex other) {
	// TODO: return true by default. This is just a hack so I dont have to create a whole new graphedit release for now.
	return false; // NOTE: only the position can change (this function is meant to be overridden)
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
