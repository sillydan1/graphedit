package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
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
 * The ViewModel representation of a graph edge.
 * Edges connects vertices with other vertices.
 */
public class ViewModelEdge implements IInspectable, ISelectable, IHoverable, IFocusable, Property<ViewModelEdge> {
    private final UUID uuid;
    private final BooleanProperty isSelected;
    private final List<Runnable> focusEventHandlers;
    private final ObjectProperty<Node> hoverElement;

    /**
     * Property pointing to the source vertex id
     */
    protected final ObjectProperty<UUID> source;

    /**
     * Property pointing to the target vertex id
     */
    protected final ObjectProperty<UUID> target;

    /**
     * Constructs a new view model edge instance
     * @param uuid The id of the edge
     * @param source the syntactic element where the edge originates from
     * @param target the syntactic element where the edge targets
     */
    public ViewModelEdge(UUID uuid, ObjectProperty<UUID> source, ObjectProperty<UUID> target) {
	this.uuid = uuid;
	this.source = source;
	this.target = target;
	this.isSelected = new SimpleBooleanProperty(false);
	this.focusEventHandlers = new ArrayList<>();
	this.hoverElement = new SimpleObjectProperty<>();
    }

    /**
     * Constructs a new view model edge instance based on a model edge instance
     * @param uuid The id of the edge
     * @param edge the model edge to convert
     */
    public ViewModelEdge(UUID uuid, ModelEdge edge) {
	this(uuid, edge.source, edge.target);
    }

    /**
     * Constructs a new view model edge instance
     * @param uuid The id of the edge
     * @param source the syntactic element where the edge originates from
     * @param target the syntactic element where the edge targets
     */
    public ViewModelEdge(UUID uuid, UUID source, UUID target) {
	this(uuid, new SimpleObjectProperty<>(source), new SimpleObjectProperty<>(target));
    }

    /**
     * Constructs a new model edge instance based on the current view model values
     * @return a new model edge instance
     */
    public ModelEdge toModel() {
	return new ModelEdge(source.get(), target.get());
    }

    /**
     * Get the source property
     * @return the source {@link UUID} property
     */
    public ObjectProperty<UUID> source() {
	return source;
    }

    /**
     * Get the target property
     * @return the target {@link UUID} property
     */
    public ObjectProperty<UUID> target() {
	return target;
    }

    /**
     * Get the id of the edge
     * @return the unique identifier of the edge
     */
    public UUID id() {
	return uuid;
    }

    /**
     * Check if the proposed target vertex is a valid target. Use this in your syntax plugins to limit which vertices can be targeted by edges
     * @param target The proposed vertex id to target
     * @param graph The graph containing this edge and the proposed target vertex
     * @return true if the target is a valid target vertex for this edge, false otherwise
     */
    public boolean isTargetValid(UUID target, ViewModelGraph graph) {
	return true;
    }

    /**
     * Check if the proposed source vertex is a valid source. Use this in your syntax plugins to limit which vertices can have edges
     * @param source The proposed vertex id to source
     * @param graph The graph containing this edge and the proposed source vertex
     * @return true if the source is a valid source vertex for this edge, false otherwise
     */
    public boolean isSourceValid(UUID source, ViewModelGraph graph) {
	return true;
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
		new InspectableProperty("Source ID", source),
		new InspectableProperty("Target ID", target));
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
    public void addListener(ChangeListener<? super ViewModelEdge> listener) {
	source.addListener((e,o,n) -> listener.changed(this,this,this));
	target.addListener((e,o,n) -> listener.changed(this,this,this));
	// NOTE: isSelected changes does not constitute a "changed" event
    }

    @Override
    public void removeListener(ChangeListener<? super ViewModelEdge> listener) {
	source.removeListener((e,o,n) -> listener.changed(this,this,this));
	target.removeListener((e,o,n) -> listener.changed(this,this,this));
    }

    @Override
    public ViewModelEdge getValue() {
	return this;
    }

    @Override
    public void addListener(InvalidationListener listener) {
	source.addListener(listener);
	target.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
	source.removeListener(listener);
	target.removeListener(listener);
    }

    @Override
    public void setValue(ViewModelEdge value) {
	source.set(value.source().get());
	target.set(value.target().get());
    }

    @Override
    public void bind(ObservableValue<? extends ViewModelEdge> observable) {
	source.bind(observable.getValue().source());
	target.bind(observable.getValue().target());
    }

    @Override
    public void unbind() {
	source.unbind();
	target.unbind();
    }

    @Override
    public boolean isBound() {
	return source.isBound() || target.isBound();
    }

    @Override
    public void bindBidirectional(Property<ViewModelEdge> other) {
	source.bindBidirectional(other.getValue().source());
	target.bindBidirectional(other.getValue().target());
    }

    @Override
    public void unbindBidirectional(Property<ViewModelEdge> other) {
	source.unbindBidirectional(other.getValue().source());
	target.unbindBidirectional(other.getValue().target());
    }

    @Override
    public void hover(Node hoverDisplay) {
	hoverElement.set(hoverDisplay);
    }

    @Override
    public void unhover() {
	hoverElement.set(null);
    }

    @Override
    public boolean isHovering() {
	return hoverElement.isNotNull().get();
    }

    @Override
    public void addHoverListener(ChangeListener<Node> consumer) {
	hoverElement.addListener(consumer);
    }

    @Override
    public boolean equals(Object other) {
	if(other == null)
	    return false;
	if(!(other instanceof ViewModelEdge vother))
	    return false;
	if(!uuid.equals(vother.uuid))
	    return false;
	if(!source.get().equals(vother.source.get()))
	    return false;
	if(!target.get().equals(vother.target.get()))
	    return false;
	return true;
    }

    @Override
    public int hashCode() {
	return uuid.hashCode() ^ source.get().hashCode() ^ target.get().hashCode();
    }
}
