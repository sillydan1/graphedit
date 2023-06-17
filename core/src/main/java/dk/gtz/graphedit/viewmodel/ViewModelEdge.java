package dk.gtz.graphedit.viewmodel;

import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import javafx.beans.property.SimpleObjectProperty;

public record ViewModelEdge(SimpleObjectProperty<UUID> source, SimpleObjectProperty<UUID> target) {
    public ViewModelEdge(ModelEdge edge) {
        this(new SimpleObjectProperty<>(edge.source()), new SimpleObjectProperty<>(edge.target()));
    }

    public ModelEdge toModel() {
        return new ModelEdge(source.get(), target.get());
    }
}

