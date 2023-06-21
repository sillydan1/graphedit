package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.undo.Undoable;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class VertexDeleteTool extends AbstractBaseTool {
    private final IUndoSystem undoSystem;

    public VertexDeleteTool() {
        this.undoSystem = DI.get(IUndoSystem.class);
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("delete vertex from the graph");
    }

	@Override
	public Node getGraphic() {
        var outerIcon = new FontIcon(BootstrapIcons.X);
        var innerIcon = new FontIcon(BootstrapIcons.PLUS_CIRCLE);
        var icon = new StackedFontIcon();
        icon.getChildren().addAll(innerIcon, outerIcon);
        return icon;
	}

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_RELEASED))
            delete(e.vertexId(), e.vertex(), e.graph());
    }

    public void delete(UUID vertexId, ViewModelVertex vertex, ViewModelGraph graph) {
        var linkedEdges = graph.edges()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().source().get().equals(vertexId) || e.getValue().target().get().equals(vertexId))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())); 

        graph.vertices().remove(vertexId);
        for(var edge : linkedEdges.entrySet()) 
            graph.edges().remove(edge.getKey());
        undoSystem.push(new Undoable("edge delete action",
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

