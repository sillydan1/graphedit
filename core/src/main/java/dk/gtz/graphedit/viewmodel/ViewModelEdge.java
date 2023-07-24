package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ViewModelEdge implements IInspectable, ISelectable, IFocusable {
    private final SimpleObjectProperty<UUID> source;
    private final SimpleObjectProperty<UUID> target;
    private final BooleanProperty isSelected;
    private final List<Runnable> focusEventHandlers;

    public ViewModelEdge(SimpleObjectProperty<UUID> source, SimpleObjectProperty<UUID> target) {
        this.source = source;
        this.target = target;
        this.isSelected = new SimpleBooleanProperty(false);
        this.focusEventHandlers = new ArrayList<>();
    }

    public ViewModelEdge(ModelEdge edge) {
        this(edge.source(), edge.target());
    }

    public ViewModelEdge(UUID source, UUID target) {
        this(new SimpleObjectProperty<>(source), new SimpleObjectProperty<>(target));
    }

    public ModelEdge toModel() {
        return new ModelEdge(source.get(), target.get());
    }

    public SimpleObjectProperty<UUID> source() {
        return source;
    }

    public SimpleObjectProperty<UUID> target() {
        return target;
    }

    @Override
    public BooleanProperty getIsSelected() {
        return isSelected;
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

