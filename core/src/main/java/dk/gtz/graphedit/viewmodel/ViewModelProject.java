package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelProject;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;

public record ViewModelProject(SimpleMapProperty<String,String> metadata, SimpleStringProperty rootDirectory, SimpleListProperty<String> excludeFiles) {
    public ViewModelProject(ModelProject modelProject) {
        this(new SimpleMapProperty<>(), new SimpleStringProperty(modelProject.rootDirectory()), new SimpleListProperty<>());
        metadata.putAll(modelProject.metadata());
        excludeFiles.addAll(modelProject.excludeFiles());
    }
}

