package dk.gtz.graphedit.viewmodel;

import java.util.Collection;
import java.util.HashMap;

import dk.gtz.graphedit.model.ModelLint;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

/**
 * A container that holds {@link ViewModelLint} instances.
 */
public class LintContainer {
    private final MapProperty<String,ListProperty<ViewModelLint>> data;

    /**
     * Construct a new lint container.
     */
    public LintContainer() {
        data = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>()));
    }

    /**
     * Get the internal map property of lints.
     * @return A subscibable map property with the lints within.
     */
    public MapProperty<String,ListProperty<ViewModelLint>> getProperty() {
        return data;
    }

    /**
     * Lookup a buffer key in the map. Will create a new entry if it does not exist yet.
     * @param bufferKey The buffer key to look up.
     * @return A list property of lints, will create a new empty list property and return it if it does not exist.
     */
    public ListProperty<ViewModelLint> get(String bufferKey) {
        if(!data.containsKey(bufferKey))
            data.put(bufferKey, new SimpleListProperty<>(FXCollections.observableArrayList()));
        return data.get(bufferKey);
    }

    /**
     * Add a new lint to the collection.
     * @param bufferKey The key of the related buffer.
     * @param lint The lint to add.
     * @return Builder-pattern style this.
     */
    public LintContainer add(String bufferKey, ViewModelLint lint) {
        if(!data.containsKey(bufferKey))
            data.put(bufferKey, new SimpleListProperty<>(FXCollections.observableArrayList()));
        data.get(bufferKey).add(lint);
        return this;
    }

    /**
     * Add a new lint to the collection.
     * @param lint The lint to add.
     * @return Builder-pattern style this.
     */
    public LintContainer add(ModelLint lint) {
        if(!data.containsKey(lint.modelKey()))
            data.put(lint.modelKey(), new SimpleListProperty<>(FXCollections.observableArrayList()));
        data.get(lint.modelKey()).add(new ViewModelLint(lint));
        return this;
    }

    /**
     * Replace the collection with a new collection of lints.
     * @param lints The lints to replace with.
     * @return Builder-pattern style this.
     */
    public LintContainer replaceAll(Collection<ModelLint> lints) {
        clear();
        for(var lint : lints)
            add(lint);
        return this;
    }

    private void clear() {
        for(var entry : data.entrySet())
            entry.getValue().clear();
    }
}
