package dk.gtz.graphedit.viewmodel;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;

public interface IHoverable {
    void hover(Node hoverDisplay);
    void unhover();
    boolean isHovering();
    void addHoverListener(ChangeListener<Node> consumer);
}
