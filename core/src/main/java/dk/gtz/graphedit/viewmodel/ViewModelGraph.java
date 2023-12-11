package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.spi.ISyntaxFactory;
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

    /**
     * Get the declarations portion of the graph.
     * The declarations are a string that can contain extraneous textual syntax such as variable declarations, readme data, value ranges, functions etc.
     * @return The string property value of the declarations
     */
    public StringProperty declarations() {
        return declarations;
    }

    /**
     * Get the vertices mapping
     * @return The mapping of vertex ids to viewmodel entries
     */
    public MapProperty<UUID, ViewModelVertex> vertices() {
        return vertices;
    }

    /**
     * Get the edges mapping
     * @return The mapping of edge ids to viewmodel entries
     */
    public MapProperty<UUID, ViewModelEdge> edges() {
        return edges;
    }

    /**
     * Checks that all the edges' sources and edges are present in the vertices collection.
     * @return True if the graph is valid, otherwise false.
     */
    public boolean isValid() {
        for(var edge : edges().entrySet()) {
            var containsSourceAndTargetVertex = vertices().containsKey(edge.getValue().source().get()) && vertices().containsKey(edge.getValue().target().get());
            if(!containsSourceAndTargetVertex)
                return false;
        }
        return true;
    }

    /**
     * Construct a new instance
     * @param declarations A string that can contain extraneous textual syntax such as variable declarations, readme data, value ranges, functions etc.
     * @param vertices A mapping of vertex ids to model vertex-values
     * @param edges A mapping of edge ids to model edge-values
     */
    public ViewModelGraph(StringProperty declarations, MapProperty<UUID,ViewModelVertex> vertices, MapProperty<UUID,ViewModelEdge> edges) {
        this.declarations = declarations;
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * Constructs a new view model graph instance based on a model graph instance
     * @param graph The model graph to convert
     * @param syntaxFactory The associated syntax factory
     */
    public ViewModelGraph(ModelGraph graph, ISyntaxFactory syntaxFactory) {
        this(new SimpleStringProperty(graph.declarations()),new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())),new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())));
        for(var v : graph.vertices().entrySet())
            vertices.put(v.getKey(), syntaxFactory.createVertexViewModel(v.getKey(), v.getValue()));
        for(var e : graph.edges().entrySet())
            edges.put(e.getKey(), syntaxFactory.createEdgeViewModel(e.getKey(), e.getValue()));
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

    /**
     * Check if the graph contains no syntactic elements
     * @return true if there are no declarations, vertices or edges
     */
    public boolean isEmpty() {
        return declarations.getValueSafe().trim().isEmpty() && vertices.isEmpty() && edges.isEmpty();
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
        ChangeListener<? super ViewModelVertex> vertexChangeListener = (e,o,n) -> listener.changed(this,this,this);
        vertices.forEach((k,v) -> v.addListener(vertexChangeListener));
        vertices.addListener((MapChangeListener<UUID,ViewModelVertex>)event -> {
            if(event.wasAdded())
                event.getValueAdded().addListener(vertexChangeListener);
            if(event.wasRemoved())
                event.getValueRemoved().removeListener(vertexChangeListener);
            listener.changed(this,this,this);
        });
        ChangeListener<? super ViewModelEdge> edgeChangeListener = (e,o,n) -> listener.changed(this,this,this);
        edges.forEach((k,v) -> v.addListener((e,o,n) -> listener.changed(this,this,this)));
        edges.addListener((MapChangeListener<UUID,ViewModelEdge>)event -> {
            if(event.wasAdded())
                event.getValueAdded().addListener(edgeChangeListener);
            if(event.wasRemoved())
                event.getValueRemoved().removeListener(edgeChangeListener);
            listener.changed(this,this,this);
        });
    }

    @Override
    public void removeListener(ChangeListener<? super ViewModelGraph> listener) {
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
