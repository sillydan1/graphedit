package dk.gtz.graphedit.viewmodel;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.model.ModelRunTarget;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

public record ViewModelProject(SimpleMapProperty<String,String> metadata, SimpleStringProperty name, SimpleStringProperty rootDirectory, SimpleBooleanProperty isSavedInTemp, SimpleListProperty<String> excludeFiles, SimpleListProperty<ViewModelRunTarget> runTargets) {
    public ViewModelProject(ModelProject modelProject, Optional<String> rootDirectory) throws IOException {
        this(new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>())), new SimpleStringProperty(modelProject.name()), new SimpleStringProperty(), new SimpleBooleanProperty(rootDirectory.isEmpty()), new SimpleListProperty<>(), new SimpleListProperty<>(FXCollections.observableArrayList()));
        metadata.putAll(modelProject.metadata());
        excludeFiles.addAll(modelProject.excludeFiles());
        if(rootDirectory.isPresent())
            this.rootDirectory.set(rootDirectory.get());
        else
            this.rootDirectory.set(Files.createTempDirectory("graphedit-").toString());
        for(var runTarget : modelProject.runTargets())
            runTargets.add(new ViewModelRunTarget(runTarget));
    }

    public ModelProject toModel() {
        var metadataMap = new HashMap<String,String>();
        for(var data : metadata().entrySet())
            metadataMap.put(data.getKey(), data.getValue());
        var excludeFiles = new ArrayList<String>();
        for(var ex : excludeFiles())
            excludeFiles.add(ex);
        var runTargets = new ArrayList<ModelRunTarget>();
        for(var runTarget : runTargets())
            runTargets.add(runTarget.toModel());
        return new ModelProject(metadataMap, name().get(), excludeFiles, runTargets);
    }
}

