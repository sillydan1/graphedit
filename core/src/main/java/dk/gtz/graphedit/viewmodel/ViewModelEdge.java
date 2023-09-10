package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The ViewModel representation of a graph edge.
 * Edges connects vertices with other vertices.
 */
public class ViewModelEdge implements IInspectable, ISelectable, IFocusable {
    private final ObjectProperty<UUID> source;
    private final ObjectProperty<UUID> target;
    private final BooleanProperty isSelected;
    private final List<Runnable> focusEventHandlers;

    /**
     * Constructs a new view model edge instance
     * @param source the syntactic element where the edge originates from
     * @param target the syntactic element where the edge targets
     */
    public ViewModelEdge(ObjectProperty<UUID> source, ObjectProperty<UUID> target) {
        this.source = source;
        this.target = target;
        this.isSelected = new SimpleBooleanProperty(false);
        this.focusEventHandlers = new ArrayList<>();
    }

    /**
     * Constructs a new view model edge instance based on a model edge instance
     * @param edge the model edge to convert
     */
    public ViewModelEdge(ModelEdge edge) {
        this(edge.source(), edge.target());
    }

    /**
     * Constructs a new view model edge instance
     * @param source the syntactic element where the edge originates from
     * @param target the syntactic element where the edge targets
     */
    public ViewModelEdge(UUID source, UUID target) {
        this(new SimpleObjectProperty<>(source), new SimpleObjectProperty<>(target));
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
     * {@inheritDoc IInspectable#getInspectableObjects()}
     * @return An unmodifiable list of inspectable objects
     * */
    @Override
    public List<InspectableProperty> getInspectableObjects() {
        return List.of(
                new InspectableProperty("Source ID", source),
                new InspectableProperty("Target ID", target));
    }
}

