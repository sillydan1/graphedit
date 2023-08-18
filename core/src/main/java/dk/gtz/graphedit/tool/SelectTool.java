package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.view.events.ViewportKeyEvent;
import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.yalibs.yadi.DI;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SelectTool extends AbstractBaseTool {
    private final Logger logger = LoggerFactory.getLogger(SelectTool.class);
    private final ObservableList<ISelectable> selectedElements;

    public SelectTool() {
        selectedElements = DI.get("selectedElements");
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("select syntactic elements");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.CURSOR);
    }

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {
        if(e.event().getEventType().equals(KeyEvent.KEY_RELEASED))
            if(e.event().getCode().equals(KeyCode.ESCAPE))
                clear();
    }

    @Override
    public void onViewportMouseEvent(ViewportMouseEvent e) {
        // TODO: Something like this would be nice, but viewport events are also fired when you click on a vertex/edge, so this doesnt work
        // if(e.event().getEventType().equals(MouseEvent.MOUSE_CLICKED))
        //     if(!e.event().isControlDown())
        //         clear();
    }

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            if(!e.event().isControlDown())
                clear();
            add(e.vertex());
        }
    }

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            if(!e.event().isControlDown())
                clear();
            add(e.edge());
        }
    }

    public void clear() {
        for(var e : selectedElements) 
            e.getIsSelected().set(false);
        selectedElements.clear();
    }

    public void add(ISelectable selectable) {
        if(selectedElements.contains(selectable)) {
            selectedElements.remove(selectable);
            selectable.getIsSelected().set(false);
        } else {
            selectedElements.add(selectable);
            selectable.getIsSelected().set(true);
        }
    }
}

