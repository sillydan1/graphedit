package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelTextVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class VertexCreateTool extends AbstractBaseTool {
    private static Logger logger = LoggerFactory.getLogger(VertexCreateTool.class);
    private final IUndoSystem undoSystem;

    public VertexCreateTool() {
        this.undoSystem = DI.get(IUndoSystem.class);
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("Create new vertex");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.PLUS_CIRCLE);
    }

    @Override
    public void onViewportMouseEvent(ViewportMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            var posX = (e.event().getX() - e.viewportAffine().getTx()) / e.viewportAffine().getMxx();
            var posY = (e.event().getY() - e.viewportAffine().getTy()) / e.viewportAffine().getMyy();
            var point = new ViewModelPoint(posX, posY);
            if(e.editorSettings().gridSnap().get())
                point.snapToGrid(e.editorSettings());
            if(e.event().isShiftDown())
                createTextVertex(point, e.graph());
            else
                createCircleVertex(point, e.graph());
        }
    }

    public void createCircleVertex(ViewModelPoint point, ViewModelGraph graph) {
        var vertex = new ViewModelVertex(point, new ViewModelVertexShape(ViewModelShapeType.OVAL));
        var id = UUID.randomUUID();
        graph.vertices().put(id, vertex);
        undoSystem.push(new Undoable("vertex create action",
                    () -> graph.vertices().remove(id),
                    () -> graph.vertices().put(id, vertex)));
    }

    public void createTextVertex(ViewModelPoint point, ViewModelGraph graph) {
        var vertex = new ViewModelTextVertex(point, new ViewModelVertexShape(ViewModelShapeType.RECTANGLE));
        var id = UUID.randomUUID();
        graph.vertices().put(id, vertex);
        undoSystem.push(new Undoable("vertex create action",
                    () -> graph.vertices().remove(id),
                    () -> graph.vertices().put(id, vertex)));
    }
}

