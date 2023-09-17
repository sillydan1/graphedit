package dk.gtz.graphedit.viewmodel;

import java.util.List;
import java.util.Map;

import dk.gtz.graphedit.model.ModelRunTarget;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/**
 * View model representation of a {@link ModelRunTarget}.
 */
public record ViewModelRunTarget(SimpleStringProperty name, SimpleStringProperty command, SimpleStringProperty currentWorkingDirectory, SimpleBooleanProperty runAsShell, SimpleListProperty<StringProperty> arguments, SimpleListProperty<ViewModelEnvironmentVariable> environment) {

    public ViewModelRunTarget(String name, String command, String currentWorkingDirectory, boolean runAsShell, List<String> arguments, Map<String,String> environment) {
        this(new SimpleStringProperty(name),
            new SimpleStringProperty(command),
            new SimpleStringProperty(currentWorkingDirectory),
            new SimpleBooleanProperty(runAsShell),
            new SimpleListProperty<>(FXCollections.observableArrayList()),
            new SimpleListProperty<>(FXCollections.observableArrayList()));
        for(var argument : arguments)
            this.arguments.add(new SimpleStringProperty(argument));
        for(var env : environment.entrySet())
            this.environment.add(new ViewModelEnvironmentVariable(new SimpleStringProperty(env.getKey()), new SimpleStringProperty(env.getValue())));
    }

    public ViewModelRunTarget(ModelRunTarget runTarget) {
        this(runTarget.name(),
                runTarget.command(),
                runTarget.cwd(),
                runTarget.runAsShell(),
                new SimpleListProperty<>(FXCollections.observableArrayList()), 
                new SimpleMapProperty<>(FXCollections.observableHashMap()));
        for(var argument : runTarget.arguments())
            this.arguments.add(new SimpleStringProperty(argument));
        for(var env : runTarget.environment().entrySet())
            this.environment.add(new ViewModelEnvironmentVariable(new SimpleStringProperty(env.getKey()), new SimpleStringProperty(env.getValue())));
    }
}

