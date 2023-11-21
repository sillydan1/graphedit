package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Tool to create vertices.
 *
 * When selected, click anywhere on the canvas to create a vertex.
 */
public class VertexCreateTool extends AbstractBaseTool {
    private static Logger logger = LoggerFactory.getLogger(VertexCreateTool.class);
    private final IUndoSystem undoSystem;

    /**
     * Construct a new instance
     */
    public VertexCreateTool() {
        this.undoSystem = DI.get(IUndoSystem.class);
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool to create vertices.

            When selected, click anywhere on the canvas to create a vertex.
            """;
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
            createCircleVertex(point, e.graph(), e.syntax());
        }
    }

    private void createCircleVertex(ViewModelPoint point, ViewModelGraph graph, ISyntaxFactory syntaxFactory) {
        var id = UUID.randomUUID();
        var vertex = syntaxFactory.createVertexViewModel(id, new ModelVertex(point.toModel()));
        graph.vertices().put(id, vertex);
        undoSystem.push(new Undoable("vertex create action",
                    () -> graph.vertices().remove(id),
                    () -> graph.vertices().put(id, vertex)));
    }
}
