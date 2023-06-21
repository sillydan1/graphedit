package dk.gtz.graphedit.viewmodel;

import java.util.List;

public interface IInspectable {
    /**
     * Gets a list of objects that should be inspectable by an {@code InspectorController} view
     * @return A list of objects
     * */
    List<InspectableProperty> getInspectableObjects();
}

