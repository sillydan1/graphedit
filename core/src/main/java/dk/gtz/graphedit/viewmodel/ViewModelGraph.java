package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelGraph;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

public record ViewModelGraph(SimpleStringProperty declarations, SimpleMapProperty<UUID,ViewModelVertex> vertices, SimpleMapProperty<UUID,ViewModelEdge> edges) {
    public ViewModelGraph(ModelGraph graph) {
        this(new SimpleStringProperty(graph.declarations()),new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())),new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())));
        for(var v : graph.vertices().entrySet())
            vertices.put(v.getKey(), new ViewModelVertex(v.getValue()));
        for(var e : graph.edges().entrySet())
            edges.put(e.getKey(), new ViewModelEdge(e.getValue()));
    }
}
            
