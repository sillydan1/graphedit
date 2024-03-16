package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.events.EdgeMouseEvent;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.Undoable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Tool to delete edges between vertices.
 *
 * When selected, click an edge to delete it.
 */
public class EdgeDeleteTool extends AbstractBaseTool {
    /**
     * Create a new instance of the edge delete tool
     */
    public EdgeDeleteTool() {
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool to delete edges between vertices.

            When selected, click an edge to delete it.
            """;
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("delete an edge from the graph");
    }

    @Override
    public Node getGraphic() {
        var outerIcon = new FontIcon(BootstrapIcons.X);
        outerIcon.getStyleClass().add(Styles.DANGER);
        var innerIcon = new FontIcon(BootstrapIcons.ARROW_DOWN_RIGHT_CIRCLE);
        var icon = new StackedFontIcon();
        icon.getChildren().addAll(innerIcon, outerIcon);
        return icon;
    }

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_RELEASED))
            delete(DI.get(IBufferContainer.class).get(e.bufferId()), e.edgeId(), e.edge(), e.graph());
    }

    /**
     * Deletes a specified edge
     * @param edgeId The id of the edge to delete
     * @param edge The edge to delete
     * @param graph The graph containing the edge to delete
     */
    public void delete(ViewModelProjectResource buffer, UUID edgeId, ViewModelEdge edge, ViewModelGraph graph) {
        var deletedEdge = graph.edges().remove(edgeId);
        if(deletedEdge == null)
            return;
        buffer.getUndoSystem().push(new Undoable("edge delete action",
                    () -> graph.edges().put(edgeId, edge),
                    () -> graph.edges().remove(edgeId)));
    }
}
