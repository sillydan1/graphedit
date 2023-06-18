package dk.gtz.graphedit.viewmodel;

import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import javafx.beans.property.SimpleObjectProperty;

public record ViewModelEdge(SimpleObjectProperty<UUID> source, SimpleObjectProperty<UUID> target) {
    public ViewModelEdge(ModelEdge edge) {
        this(edge.source(), edge.target());
    }
    
    public ViewModelEdge(UUID source, UUID target) {
        this(new SimpleObjectProperty<>(source), new SimpleObjectProperty<>(target));
    }

    public ModelEdge toModel() {
        return new ModelEdge(source.get(), target.get());
    }
}

