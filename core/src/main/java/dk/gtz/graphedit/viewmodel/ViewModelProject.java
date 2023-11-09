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

/**
 * Viewmodel version of the on-disk file structure for a project file
 *
 * Not to be confused with {@link ViewModelProjectResource}, this class represents a graphedit project.
 * @param metadata A map of generic string-encoded metadata, useful for syntax=specific data
 * @param name The name of the project
 * @param rootDirectory The directory containing the on-disk file
 * @param isSavedInTemp Whether or not the project is currently a temporary project
 * @param excludeFiles A list of files to exclude from the project, can be glob-specifications
 * @param runTargets A list of {@link ViewModelRunTarget} associated with this project
 */
public record ViewModelProject(
        SimpleListProperty<Pair<StringProperty,StringProperty>> metadata,
        SimpleStringProperty name,
        SimpleStringProperty rootDirectory,
        SimpleBooleanProperty isSavedInTemp,
        SimpleListProperty<StringProperty> excludeFiles,
        SimpleListProperty<ViewModelRunTarget> runTargets) {

    /**
     * Constructs a new viewmodel project based on a model instance and an optional rootdirectory
     * @param modelProject The model project datastructure to base on
     * @param rootDirectory When provided, a valid file path containing the associated model project file, else will create a new temp directory for you
     * @throws IOException when temporary directory creation failed
     */
    public ViewModelProject(ModelProject modelProject, Optional<String> rootDirectory) throws IOException {
        this(
                new SimpleListProperty<>(FXCollections.observableArrayList()),
                new SimpleStringProperty(modelProject.name()),
                new SimpleStringProperty(),
                new SimpleBooleanProperty(rootDirectory.isEmpty()),
                new SimpleListProperty<>(FXCollections.observableArrayList()),
                new SimpleListProperty<>(FXCollections.observableArrayList())
            );
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

    /**
     * Converts this viewmodel into a model type
     * @return A model version of this project data
     */
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
