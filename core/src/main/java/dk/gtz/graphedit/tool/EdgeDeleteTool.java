package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.undo.Undoable;
import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class EdgeDeleteTool extends AbstractBaseTool {
    private final IUndoSystem undoSystem;

    public EdgeDeleteTool() {
        this.undoSystem = DI.get(IUndoSystem.class);
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("delete an edge from the graph");
    }

	@Override
	public Node getGraphic() {
        var outerIcon = new FontIcon(BootstrapIcons.X);
        var innerIcon = new FontIcon(BootstrapIcons.ARROW_DOWN_RIGHT_CIRCLE);
        var icon = new StackedFontIcon();
        icon.getChildren().addAll(innerIcon, outerIcon);
        return icon;
	}

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_RELEASED))
            delete(e.edgeId(), e.edge(), e.graph());
    }

    public void delete(UUID edgeId, ViewModelEdge edge, ViewModelGraph graph) {
        graph.edges().remove(edgeId);
        undoSystem.push(new Undoable("edge delete action",
                    () -> graph.edges().put(edgeId, edge),
                    () -> graph.edges().remove(edgeId)));
    }
}
