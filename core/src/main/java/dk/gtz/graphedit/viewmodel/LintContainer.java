package dk.gtz.graphedit.viewmodel;

import java.util.Collection;
import java.util.HashMap;

import dk.gtz.graphedit.model.ModelLint;
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

    public LintContainer add(ModelLint lint) {
        if(!data.containsKey(lint.modelKey()))
            data.put(lint.modelKey(), new SimpleListProperty<>(FXCollections.observableArrayList()));
        data.get(lint.modelKey()).add(new ViewModelLint(lint));
        return this;
    }

    public LintContainer replaceAll(Collection<ModelLint> lints) {
        data.clear();
        for(var lint : lints)
            add(lint);
        return this;
    }
}
