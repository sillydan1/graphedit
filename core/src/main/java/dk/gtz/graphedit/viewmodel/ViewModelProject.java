package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;

import dk.gtz.graphedit.model.ModelProject;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

public record ViewModelProject(SimpleMapProperty<String,String> metadata, SimpleStringProperty name, SimpleStringProperty rootDirectory, SimpleListProperty<String> excludeFiles) {
    public ViewModelProject(ModelProject modelProject) {
        this(new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())), new SimpleStringProperty(modelProject.name()), new SimpleStringProperty(modelProject.rootDirectory()), new SimpleListProperty<>());
            metadata.putAll(modelProject.metadata());
        excludeFiles.addAll(modelProject.excludeFiles());
    }
}
