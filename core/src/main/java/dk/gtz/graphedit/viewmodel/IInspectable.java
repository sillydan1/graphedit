package dk.gtz.graphedit.viewmodel;

import java.util.List;

import dk.gtz.graphedit.util.InspectorUtils;

/**
 * Interface for objects that can be inspected and edited with {@link InspectorUtils}
 */
public interface IInspectable {
    /**
     * Gets a list of objects that should be inspectable by an {@code InspectorController} view
     * @return A list of objects
     * */
    List<InspectableProperty> getInspectableObjects();
}

