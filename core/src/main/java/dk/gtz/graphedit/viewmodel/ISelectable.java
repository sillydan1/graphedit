package dk.gtz.graphedit.viewmodel;

import javafx.beans.property.BooleanProperty;

public interface ISelectable {
    BooleanProperty getIsSelected();
    void select();
    void deselect();
}

