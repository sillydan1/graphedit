package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
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
public record ViewModelRunTarget(SimpleStringProperty name, SimpleStringProperty command, SimpleStringProperty currentWorkingDirectory, SimpleBooleanProperty runAsShell, SimpleBooleanProperty saveBeforeRun, SimpleListProperty<StringProperty> arguments, SimpleListProperty<ViewModelEnvironmentVariable> environment) {

    public ViewModelRunTarget(String name, String command, String currentWorkingDirectory, boolean runAsShell, boolean saveBeforeRun, List<String> arguments, Map<String,String> environment) {
        this(new SimpleStringProperty(name),
            new SimpleStringProperty(command),
            new SimpleStringProperty(currentWorkingDirectory),
            new SimpleBooleanProperty(runAsShell),
            new SimpleBooleanProperty(saveBeforeRun),
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
                runTarget.saveBeforeRun(),
                new SimpleListProperty<>(FXCollections.observableArrayList()), 
                new SimpleMapProperty<>(FXCollections.observableHashMap()));
        for(var argument : runTarget.arguments())
            this.arguments.add(new SimpleStringProperty(argument));
        for(var env : runTarget.environment().entrySet())
            this.environment.add(new ViewModelEnvironmentVariable(new SimpleStringProperty(env.getKey()), new SimpleStringProperty(env.getValue())));
    }

    public ModelRunTarget toModel() {
        var args = new ArrayList<String>(arguments().size());
        for(var arg : arguments())
            args.add(arg.get());
        var envs = new HashMap<String, String>();
        for(var env : environment())
            envs.put(env.key().get(), env.value().get());
        return new ModelRunTarget(
                name().get(),
                command().get(),
                args,
                currentWorkingDirectory().get(),
                runAsShell().get(),
                saveBeforeRun().get(),
                envs);
    }
}

