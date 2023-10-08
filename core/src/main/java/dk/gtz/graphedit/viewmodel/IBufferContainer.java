package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.view.ISyntaxFactory;
import javafx.collections.ObservableMap;

/**
 * A container that holds {@link ViewModelProjectResource} instances.
 */
public interface IBufferContainer {
    /**
     * Get an instance with a lookup key
     * @param key the key that the project resource was loaded / inserted with
     * @return the associated project resource instance
     * @throws Exception ultimately up to the implementation, but this could be that there is no instance associated with the porovided key
     */
    ViewModelProjectResource get(String key) throws Exception;

    /**
     * Close and remove a project resource instance from the collection.
     * This will fire an event to all event handlers.
     * If the key is not present in the collection, this action should do nothing
     * @param key the key to remove
     */
    void close(String key);

    /**
     * Open and add a new project resource instance to the collection.
     * This will fire an event to all event handlers.
     * This will try to infer the project resource from the key.
     * @param key the key to open
     * @throws Exception ultimately up to the implementation, but this could be that the key already exists or that the resource could not be inferred
     */
    void open(String key) throws Exception;

    /**
     * Open and add a new project resource instance to the collection.
     * This will fire an event to all event handlers.
     * @param key the key to open
     * @param model the project resource to insert
     * @throws Exception ultimately up to the implementation, but this could be that the key already exists
     */
    void open(String key, ViewModelProjectResource model) throws Exception;

    /**
     * Check if the collection contains a project resource instance with the provided key
     * @param key the key to lookup
     * @return {@code true} if the collection contains a project resource instance with the associated key, otherwise {@code false}
     */
    boolean contains(String key);

    /**
     * Get the underlying obserable buffer map.
     * This is useful if you want to add eventhandlers to certain events.
     * @return the underlying obserable map
     */
    ObservableMap<String,ViewModelProjectResource> getBuffers();
}

