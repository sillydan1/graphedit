package dk.gtz.graphedit.viewmodel;

import java.util.UUID;

import dk.gtz.graphedit.model.ModelGraph;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;

public record ViewModelGraph(
        SimpleStringProperty declarations,
        SimpleMapProperty<UUID,ViewModelVertex> vertices,
        SimpleMapProperty<UUID,ViewModelEdge> edges) {
    public ViewModelGraph(ModelGraph graph) {
        this(); // TODO: uhh..
    }
}
            
