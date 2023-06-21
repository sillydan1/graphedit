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
import dk.gtz.graphedit.view.MouseTracker;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.view.events.ViewportKeyEvent;
import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EdgeCreateTool extends AbstractBaseTool {
    private static Logger logger = LoggerFactory.getLogger(EdgeCreateTool.class);
    private Optional<UUID> currenEdgeId;
    private Optional<ViewModelEdge> currentEdge;
    private final IUndoSystem undoSystem;

    public EdgeCreateTool() {
        this.currenEdgeId = Optional.empty();
        this.currentEdge = Optional.empty();
        this.undoSystem = DI.get(IUndoSystem.class);
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("Create new edge between two vertices");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.ARROW_DOWN_RIGHT_CIRCLE);
    }

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) { // TODO: Tools should also get keyboard events (e.g. esc for cancel)
        if(e.event().getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            if(!isCurrentlyCreatingEdge()) {
                create(e.vertexId(), e.graph());
                return;
            }
            if(e.vertexId().equals(currentEdge.get().source().get())) {
                cancel(e.graph());
                return;
            }
            release(e.vertexId(), e.graph());
        }

        if(e.event().getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            if(!isCurrentlyCreatingEdge())
                return;
            if(e.vertexId().equals(currentEdge.get().source().get()))
                return;
            release(e.vertexId(), e.graph());
        }
    }

    @Override
    public void onViewportMouseEvent(ViewportMouseEvent e) {
        var tracker = DI.get(MouseTracker.class);
        tracker.getXProperty().set(e.event().getX());
        tracker.getYProperty().set(e.event().getY());
    }

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {
        if(e.event().getEventType().equals(KeyEvent.KEY_RELEASED))
            if(e.event().getCode() == KeyCode.ESCAPE)
                cancel(e.graph());
    }

    public boolean isCurrentlyCreatingEdge() {
        return currentEdge.isPresent();
    }

    public void cancel(ViewModelGraph graph) {
        if(currenEdgeId.isEmpty())
            return;
        graph.edges().remove(currenEdgeId.get());
        clear();
    }

    public void release(UUID releaseTarget, ViewModelGraph graph) {
        currentEdge.get().target().set(releaseTarget);
        var currentEdgeIdCopy = currenEdgeId.get();
        var currentEdgeCopy = currentEdge.get();
        undoSystem.push(new Undoable("add edge action",
                    () -> graph.edges().remove(currentEdgeIdCopy),
                    () -> graph.edges().put(currentEdgeIdCopy, currentEdgeCopy)));
        clear();
    }

    public void create(UUID sourceTarget, ViewModelGraph graph) {
        var tracker = DI.get(MouseTracker.class);
        currenEdgeId = Optional.of(UUID.randomUUID());
        currentEdge = Optional.of(new ViewModelEdge(sourceTarget, tracker.getTrackerUUID())); // TODO: Creation should be done by a factory
        graph.edges().put(currenEdgeId.get(), currentEdge.get());
    }

    private void clear() {
        currenEdgeId = Optional.empty();
        currentEdge = Optional.empty();
    }
}

