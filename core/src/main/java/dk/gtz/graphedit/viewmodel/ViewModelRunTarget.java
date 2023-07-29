package dk.gtz.graphedit.viewmodel;

import java.util.List;
import java.util.Map;

import dk.gtz.graphedit.model.ModelRunTarget;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public record ViewModelRunTarget(SimpleStringProperty name, SimpleStringProperty command, SimpleListProperty<StringProperty> arguments, SimpleMapProperty<StringProperty, StringProperty> environment) {
    public ViewModelRunTarget(String name, String command, List<String> arguments, Map<String,String> environment) {
        this(new SimpleStringProperty(name), new SimpleStringProperty(command), new SimpleListProperty<>(FXCollections.observableArrayList()), new SimpleMapProperty<>(FXCollections.observableHashMap()));
        for(var argument : arguments)
            this.arguments.add(new SimpleStringProperty(argument));
        for(var env : environment.entrySet())
            this.environment.put(new SimpleStringProperty(env.getKey()), new SimpleStringProperty(env.getValue()));
    }
    public ViewModelRunTarget(ModelRunTarget runTarget) {
        this(runTarget.name(), runTarget.command(), new SimpleListProperty<>(FXCollections.observableArrayList()), new SimpleMapProperty<>(FXCollections.observableHashMap()));
        for(var argument : runTarget.arguments())
            this.arguments.add(new SimpleStringProperty(argument));
        for(var env : runTarget.environment().entrySet())
            this.environment.put(new SimpleStringProperty(env.getKey()), new SimpleStringProperty(env.getValue()));
    }
}

