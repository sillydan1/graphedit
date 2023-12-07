package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

public class LintContainer {
    private final MapProperty<String,ListProperty<ViewModelLint>> data;

    public LintContainer() {
        data = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>()));
    }

    public MapProperty<String,ListProperty<ViewModelLint>> getProperty() {
        return data;
    }

    public ListProperty<ViewModelLint> get(String bufferKey) {
        if(!data.containsKey(bufferKey))
            data.put(bufferKey, new SimpleListProperty<>(FXCollections.observableArrayList()));
        return data.get(bufferKey);
    }

    public LintContainer add(String bufferKey, ViewModelLint lint) {
        if(!data.containsKey(bufferKey))
            data.put(bufferKey, new SimpleListProperty<>(FXCollections.observableArrayList()));
        data.get(bufferKey).add(lint);
        return this;
    }
}
