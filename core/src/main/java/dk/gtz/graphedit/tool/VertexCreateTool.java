package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.undo.Undoable;
import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelTextVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
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
                snapToGrid(point, e.editorSettings());
            if(e.event().isShiftDown())
                createTextVertex(point, e.graph());
            else
                createCircleVertex(point, e.graph());
        }
    }

    // TODO: Move this to somewhere so it can be used by other tools as well
    private void snapToGrid(ViewModelPoint point, ViewModelEditorSettings settings) {
        point.getXProperty().set(point.getX() - (point.getX() % settings.gridSizeX().get()));
        point.getYProperty().set(point.getY() - (point.getY() % settings.gridSizeY().get()));
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

