package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;

import dk.gtz.graphedit.model.ModelProjectResource;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

public record ViewModelProjectResource(SimpleMapProperty<String,String> metadata, ViewModelGraph syntax) {
    public ViewModelProjectResource(ModelProjectResource projectResource) {
        this(new SimpleMapProperty<>(FXCollections.observableHashMap()), new ViewModelGraph(projectResource.syntax()));
        metadata.putAll(projectResource.metadata());
    }

    public ModelProjectResource toModel() {
        return new ModelProjectResource(new HashMap<>(metadata.get()), syntax.toModel());
    }
}

