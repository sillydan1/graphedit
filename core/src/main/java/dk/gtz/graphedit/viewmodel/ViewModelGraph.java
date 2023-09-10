package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import dk.gtz.graphedit.model.ModelGraph;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

/**
 * View model representation of a graphedit graph
 */
public record ViewModelGraph(SimpleStringProperty declarations, SimpleMapProperty<UUID,ViewModelVertex> vertices, SimpleMapProperty<UUID,ViewModelEdge> edges) {
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
}
 
