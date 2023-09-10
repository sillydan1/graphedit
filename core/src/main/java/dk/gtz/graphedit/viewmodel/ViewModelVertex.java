package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;

import dk.gtz.graphedit.model.ModelVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ViewModelVertex implements IInspectable, ISelectable, IFocusable {
    private final ViewModelPoint position;
    private final ViewModelVertexShape shape;
    private final BooleanProperty isSelected;
    private final List<Runnable> focusEventHandlers;

    /**
     * Constructs a new view model vertex based on a position and a shape
     * @param position the point at which the vertex is located
     * @param shape the shape at which edges should follow
     */
    public ViewModelVertex(ViewModelPoint position, ViewModelVertexShape shape) {
        this.position = position;
        this.shape = shape;
        this.isSelected = new SimpleBooleanProperty(false);
        this.focusEventHandlers = new ArrayList<>();
    }

    public ViewModelVertex(ModelVertex vertex, ViewModelVertexShape shape) {
        this(new ViewModelPoint(vertex.position()), shape);
    }

    public ViewModelVertex(ModelVertex vertex) {
        this(vertex, new ViewModelVertexShape());
    }

    public ModelVertex toModel() {
        return new ModelVertex(position.toModel());
    }

    public ViewModelPoint position() {
        return position;
    }

    public ViewModelVertexShape shape() {
        return shape;
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
                new InspectableProperty("Position X", position.getXProperty()),
                new InspectableProperty("Position Y", position.getYProperty()),
                new InspectableProperty("Shape Width", shape.widthProperty()),
                new InspectableProperty("Shape Height", shape.heightProperty()),
                new InspectableProperty("Shape Scale X", shape.scaleXProperty()),
                new InspectableProperty("Shape Scale Y", shape.scaleYProperty()));
    }
}

