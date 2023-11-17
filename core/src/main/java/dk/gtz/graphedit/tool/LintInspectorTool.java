package dk.gtz.graphedit.tool;

import java.util.List;
import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.events.EdgeMouseEvent;
import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.LintContainer;
import dk.gtz.graphedit.viewmodel.ViewModelLint;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LintInspectorTool extends AbstractBaseTool {
    private static Logger logger = LoggerFactory.getLogger(LintInspectorTool.class);
    private final LintContainer lints;

    public LintInspectorTool() {
        this.lints = DI.get(LintContainer.class);
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("Inspect lints");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.STAR);
    }

    @Override
    public String getHelpDescription() {
        return """
                Tool to inspect lints of vertices.
                
                When selected, simply hover over a vertex, and all the lints that are affecting
                that vertex will be displayed in a context menu
                """;
    }

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
            var affectingLints = lints.get(e.bufferId()).stream().filter(l -> l.affectedElements().contains(e.vertexId())).toList();
            if(!affectingLints.isEmpty())
                e.vertex().hover(createLintList(affectingLints));
        }
    }

    @Override
    public void onEdgeMouseEvent(EdgeMouseEvent e) {
        if(e.event().getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
            var affectingLints = lints.get(e.bufferId()).stream().filter(l -> l.affectedElements().contains(e.edgeId())).toList();
            if(!affectingLints.isEmpty())
                e.edge().hover(createLintList(affectingLints));
        }
    }

    private Node createLintList(List<ViewModelLint> lints) {
        var result = new VBox();
        result.setSpacing(5);
        for(var lint : lints)
            result.getChildren().addAll(createLint(lint), new Separator());
        return result;
    }

    private Node createLint(ViewModelLint lint) {
        var box = new VBox();
        var icon = new FontIcon(BootstrapIcons.INFO_CIRCLE);
        switch(lint.severity().get()) {
            case ERROR:
                icon = new FontIcon(BootstrapIcons.X_CIRCLE);
                icon.getStyleClass().add(Styles.DANGER);
                break;
            case WARNING:
                icon = new FontIcon(BootstrapIcons.EXCLAMATION_CIRCLE);
                icon.getStyleClass().add(Styles.WARNING);
                break;
            case INFO:
                icon = new FontIcon(BootstrapIcons.INFO_CIRCLE);
                break;
            default: break;
        }
        var title = new HBox(icon, new Label(lint.title().get()));
        title.setSpacing(5);
        title.setCenterShape(true);
        title.getStyleClass().add(Styles.TITLE_4);
        var body = new Text(lint.message().get());
        box.getChildren().addAll(title, body);
        VBox.setVgrow(title, Priority.ALWAYS);
        VBox.setVgrow(body, Priority.ALWAYS);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }
}
