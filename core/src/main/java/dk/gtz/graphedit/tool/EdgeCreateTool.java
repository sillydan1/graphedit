package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.util.MouseTracker;
import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Tool to create edges between vertices.
 * When selected, click a vertex to start creating an edge and complete the edge by clicking another vertex.
 * You can cancel edge creation by clicking at the canvas, the initial vertex again or by pressing <ESC>
 *
 * Note that the action completes at edge completion (second click) rather than edge creation (first click).
 */
public class EdgeCreateTool extends AbstractBaseTool {
    private static Logger logger = LoggerFactory.getLogger(EdgeCreateTool.class);
    private Optional<UUID> currenEdgeId;
    private Optional<ViewModelEdge> currentEdge;
    private final IUndoSystem undoSystem;

    /**
     * Create a new instance of {@link EdgeCreateTool}
     */
    public EdgeCreateTool() {
        this.currenEdgeId = Optional.empty();
        this.currentEdge = Optional.empty();
        this.undoSystem = DI.get(IUndoSystem.class);
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool to create edges between vertices.

            When selected, click a vertex to start creating an edge and complete the edge by clicking another vertex.
            You can cancel edge creation by clicking at the canvas, the initial vertex again or by pressing <ESC>

            Note that the action completes at edge completion (second click) rather than edge creation (first click).
            """;
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
    public void onVertexMouseEvent(VertexMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            if(!isCurrentlyCreatingEdge()) {
                create(e.vertexId(), e.graph(), e.syntax());
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

    /**
     * Check if the tool is currently creating a new edge or not
     * @return true when the tool is in a state where an edge is being created, otherwise false
     */
    public boolean isCurrentlyCreatingEdge() {
        return currentEdge.isPresent();
    }

    /**
     * Stops / cancels the edge creation process
     * @param graph The graph where the current temporary edge is located in
     */
    public void cancel(ViewModelGraph graph) {
        if(isCurrentlyCreatingEdge())
            graph.edges().remove(currenEdgeId.get());
        clear();
    }

    private boolean isReleaseTargetAndSourceTargetInSameGraph(UUID source, UUID target, ViewModelGraph graph) {
        return graph.vertices().containsKey(source) && graph.vertices().containsKey(target);
    }

    /**
     * Finishes the edge creation process.
     * Will fail with a warning if the provided graph is not the same one you started creating the edge in
     * @param releaseTarget Id of the target vertex to finalize the edge to
     * @param graph The graph where the current temporary edge is located in
     */
    public void release(UUID releaseTarget, ViewModelGraph graph) {
        if(!isReleaseTargetAndSourceTargetInSameGraph(currentEdge.get().source().get(), releaseTarget, graph)) {
            logger.warn("edge release target is not in the same graph as the source target");
            return;
        }
        if(!currentEdge.get().isTargetValid(releaseTarget, graph))
            return;
        currentEdge.get().target().set(releaseTarget);
        var currentEdgeIdCopy = currenEdgeId.get();
        var currentEdgeCopy = currentEdge.get();
        undoSystem.push(new Undoable("add edge action",
                    () -> graph.edges().remove(currentEdgeIdCopy),
                    () -> graph.edges().put(currentEdgeIdCopy, currentEdgeCopy)));
        clear();
    }

    /**
     * Start the edge creation process.
     * @param sourceTarget Id of the source vertex to start creating an edge from
     * @param graph The graph where the temporary edge shall be located in
     * @param factory The associated syntax factory
     */
    public void create(UUID sourceTarget, ViewModelGraph graph, ISyntaxFactory factory) {
        var tracker = DI.get(MouseTracker.class);
        currenEdgeId = Optional.of(UUID.randomUUID());
        currentEdge = Optional.of(factory.createEdgeViewModel(new ModelEdge(sourceTarget, tracker.getTrackerUUID())));
        if(!currentEdge.get().isSourceValid(sourceTarget, graph)) {
            clear();
            return;
        }
        graph.edges().put(currenEdgeId.get(), currentEdge.get());
    }

    private void clear() {
        currenEdgeId = Optional.empty();
        currentEdge = Optional.empty();
    }
}
