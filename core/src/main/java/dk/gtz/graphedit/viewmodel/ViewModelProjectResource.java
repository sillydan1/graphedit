package dk.gtz.graphedit.viewmodel;

import javax.swing.text.View;

import dk.gtz.graphedit.model.ModelProjectResource;
import javafx.beans.property.SimpleMapProperty;

public record ViewModelProjectResource(SimpleMapProperty<String,String> metadata, ViewModelGraph syntax) {
    public ViewModelProjectResource(ModelProjectResource projectResource) {
        this(new SimpleMapProperty<String,String>(projectResource.metadata()), new ViewModelGraph(projectResource.syntax()));
    }
}
