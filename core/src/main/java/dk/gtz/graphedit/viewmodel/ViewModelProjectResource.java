package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelProjectResource;
import javafx.beans.property.SimpleMapProperty;

public record ViewModelProjectResource(SimpleMapProperty<String,String> metadata, ViewModelGraph syntax) {
    public ViewModelProjectResource(ModelProjectResource projectResource) {
        this(new SimpleMapProperty<>(), new ViewModelGraph(projectResource.syntax()));
        metadata.putAll(projectResource.metadata());
    }
}

