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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.util.Pair;

public record ViewModelProject(SimpleListProperty<Pair<StringProperty,StringProperty>> metadata, SimpleStringProperty name, SimpleStringProperty rootDirectory, SimpleBooleanProperty isSavedInTemp, SimpleListProperty<StringProperty> excludeFiles, SimpleListProperty<ViewModelRunTarget> runTargets) {

    public ViewModelProject(ModelProject modelProject, Optional<String> rootDirectory) throws IOException {
        this(new SimpleListProperty<>(FXCollections.observableArrayList()), new SimpleStringProperty(modelProject.name()), new SimpleStringProperty(), new SimpleBooleanProperty(rootDirectory.isEmpty()), new SimpleListProperty<>(FXCollections.observableArrayList()), new SimpleListProperty<>(FXCollections.observableArrayList()));
        for(var data : modelProject.metadata().entrySet())
            metadata.add(new Pair<>(new SimpleStringProperty(data.getKey()), new SimpleStringProperty(data.getValue())));
        for(var file : modelProject.excludeFiles())
            excludeFiles.add(new SimpleStringProperty(file));
        if(rootDirectory.isPresent())
            this.rootDirectory.set(rootDirectory.get());
        else
            this.rootDirectory.set(Files.createTempDirectory("graphedit-").toString());
        for(var runTarget : modelProject.runTargets())
            runTargets.add(new ViewModelRunTarget(runTarget));
    }

    public ModelProject toModel() {
        var metadataMap = new HashMap<String,String>();
        for(var data : metadata())
            metadataMap.put(data.getKey().get(), data.getValue().get());
        var excludeFiles = new ArrayList<String>();
        for(var ex : excludeFiles())
            excludeFiles.add(ex.get());
        var runTargets = new ArrayList<ModelRunTarget>();
        for(var runTarget : runTargets())
            runTargets.add(runTarget.toModel());
        return new ModelProject(metadataMap, name().get(), excludeFiles, runTargets);
    }
}

