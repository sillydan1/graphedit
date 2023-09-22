package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import dk.gtz.graphedit.model.ModelGraph;
import javafx.beans.InvalidationListener;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;

/**
 * View model representation of a graphedit graph
 */
public class ViewModelGraph implements Property<ViewModelGraph> {
    private StringProperty declarations;
    private MapProperty<UUID,ViewModelVertex> vertices;
    private MapProperty<UUID,ViewModelEdge> edges;

    public StringProperty declarations() {
        return declarations;
    }

    public MapProperty<UUID, ViewModelVertex> vertices() {
        return vertices;
    }

    public MapProperty<UUID, ViewModelEdge> edges() {
        return edges;
    }

    public ViewModelGraph(StringProperty declarations, MapProperty<UUID,ViewModelVertex> vertices, MapProperty<UUID,ViewModelEdge> edges) {
        this.declarations = declarations;
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * Constructs a new view model graph instance based on a model graph instance
     * @param graph the model graph to convert
     */
    public ViewModelGraph(ModelGraph graph) {
        this(new SimpleStringProperty(graph.declarations()),new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())),new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())));
        for(var v : graph.vertices().entrySet())
            vertices.put(v.getKey(), new ViewModelVertex(v.getValue(), new ViewModelVertexShape(ViewModelShapeType.OVAL)));
        for(var e : graph.edges().entrySet())
            edges.put(e.getKey(), new ViewModelEdge(e.getValue()));
    }

    /**
     * Constructs a new model graph instance based on the current view model values
     * @return a new model graph instance
     */
    public ModelGraph toModel() {
        return new ModelGraph(declarations.get(), 
                vertices.get().entrySet().stream().collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().toModel())),
                edges.get().entrySet().stream().collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().toModel())));
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
    public void addListener(ChangeListener<? super ViewModelGraph> listener) {
        declarations.addListener((e,o,n) -> listener.changed(this,this,this));
        vertices.forEach((k,v) -> v.addListener((e,o,n) -> listener.changed(this,this,this)));
        vertices.addListener((MapChangeListener<UUID,ViewModelVertex>)event -> {
            if(event.wasAdded())
                event.getValueAdded().addListener((e,o,n) -> listener.changed(this,this,this));
            if(event.wasRemoved())
                event.getValueRemoved().removeListener((e,o,n) -> listener.changed(this,this,this));
            listener.changed(this,this,this);
        });
        edges.forEach((k,v) -> v.addListener((e,o,n) -> listener.changed(this,this,this)));
        edges.addListener((MapChangeListener<UUID,ViewModelEdge>)event -> {
            if(event.wasAdded())
                event.getValueAdded().addListener((e,o,n) -> listener.changed(this,this,this));
            if(event.wasRemoved())
                event.getValueRemoved().removeListener((e,o,n) -> listener.changed(this,this,this));
            listener.changed(this,this,this);
        });
    }

    @Override
    public void removeListener(ChangeListener<? super ViewModelGraph> listener) {
        // TODO: This is very unlikely to work
        declarations.removeListener((e,o,n) -> listener.changed(this,this,this));
        vertices.forEach((k,v) -> v.removeListener((e,o,n) -> listener.changed(this,this,this)));
        vertices.removeListener((MapChangeListener<UUID,ViewModelVertex>)event -> {
            if(event.wasAdded())
                event.getValueAdded().addListener((e,o,n) -> listener.changed(this,this,this));
            if(event.wasRemoved())
                event.getValueRemoved().removeListener((e,o,n) -> listener.changed(this,this,this));
            listener.changed(this,this,this);
        });
        edges.forEach((k,v) -> v.removeListener((e,o,n) -> listener.changed(this,this,this)));
        edges.removeListener((MapChangeListener<UUID,ViewModelEdge>)event -> {
            if(event.wasAdded())
                event.getValueAdded().addListener((e,o,n) -> listener.changed(this,this,this));
            if(event.wasRemoved())
                event.getValueRemoved().removeListener((e,o,n) -> listener.changed(this,this,this));
            listener.changed(this,this,this);
        });
    }

    @Override
    public ViewModelGraph getValue() {
        return this;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        declarations.addListener(listener);
        vertices.addListener(listener);
        edges.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        declarations.removeListener(listener);
        vertices.removeListener(listener);
        edges.removeListener(listener);
    }

    @Override
    public void setValue(ViewModelGraph value) {
        declarations.setValue(value.declarations().getValue());
        vertices.setValue(value.vertices().getValue());
        edges.setValue(value.edges().getValue());
    }

    @Override
    public void bind(ObservableValue<? extends ViewModelGraph> observable) {
        declarations.bind(observable.getValue().declarations());
        vertices.bind(observable.getValue().vertices());
        edges.bind(observable.getValue().edges());
    }

    @Override
    public void unbind() {
        declarations.unbind();
        vertices.unbind();
        edges.unbind();
    }

    @Override
    public boolean isBound() {
        return declarations.isBound() || vertices.isBound() || edges.isBound();
    }

    @Override
    public void bindBidirectional(Property<ViewModelGraph> other) {
        declarations.bindBidirectional(other.getValue().declarations());
        vertices.bindBidirectional(other.getValue().vertices());
        edges.bindBidirectional(other.getValue().edges());
    }

    @Override
    public void unbindBidirectional(Property<ViewModelGraph> other) {
        declarations.unbindBidirectional(other.getValue().declarations());
        vertices.unbindBidirectional(other.getValue().vertices());
        edges.unbindBidirectional(other.getValue().edges());
    }
}

