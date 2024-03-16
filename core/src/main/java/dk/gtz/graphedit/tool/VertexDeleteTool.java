package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.Undoable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Tool to delete vertices.
 *
 * When selected, click an vertex to delete it.
 */
public class VertexDeleteTool extends AbstractBaseTool {
    /**
     * Construct a new instance
     */
    public VertexDeleteTool() {
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool to delete vertices.

            When selected, click an vertex to delete it.
            """;
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("delete vertex from the graph");
    }

    @Override
    public Node getGraphic() {
        var outerIcon = new FontIcon(BootstrapIcons.X);
        outerIcon.getStyleClass().add(Styles.DANGER);
        var innerIcon = new FontIcon(BootstrapIcons.PLUS_CIRCLE);
        var icon = new StackedFontIcon();
        icon.getChildren().addAll(innerIcon, outerIcon);
        return icon;
    }

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_RELEASED))
            delete(DI.get(IBufferContainer.class).get(e.bufferId()), e.vertexId(), e.vertex(), e.graph());
    }

    /**
     * Delete a specified vertex
     * @param vertexId The id of the vertex to delete
     * @param vertex The viewmodel object of the vertex to delete
     * @param graph The graph that contains the vertex
     */
    public void delete(ViewModelProjectResource buffer, UUID vertexId, ViewModelVertex vertex, ViewModelGraph graph) {
        var linkedEdges = graph.edges()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().source().get().equals(vertexId) || e.getValue().target().get().equals(vertexId))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())); 
        var deletedVertex = graph.vertices().remove(vertexId);
        if(deletedVertex == null)
            return;
        for(var edge : linkedEdges.entrySet()) 
            graph.edges().remove(edge.getKey());
        buffer.getUndoSystem().push(new Undoable("edge delete action",
                    () -> {
                        graph.vertices().put(vertexId, vertex);
                        graph.edges().putAll(linkedEdges);
                    },
                    () -> {
                        graph.vertices().remove(vertexId);
                        for(var edge : linkedEdges.entrySet()) 
                            graph.edges().remove(edge.getKey());
                    }));
    }
}
