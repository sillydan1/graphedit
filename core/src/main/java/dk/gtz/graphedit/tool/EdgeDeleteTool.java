package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.view.events.EdgeMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class EdgeDeleteTool extends AbstractBaseTool {
    private final IUndoSystem undoSystem;

    public EdgeDeleteTool() {
        this.undoSystem = DI.get(IUndoSystem.class);
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
            delete(e.edgeId(), e.edge(), e.graph());
    }

    public void delete(UUID edgeId, ViewModelEdge edge, ViewModelGraph graph) {
        var deletedEdge = graph.edges().remove(edgeId);
        if(deletedEdge == null)
            return;
        undoSystem.push(new Undoable("edge delete action",
                    () -> graph.edges().put(edgeId, edge),
                    () -> graph.edges().remove(edgeId)));
    }
}

