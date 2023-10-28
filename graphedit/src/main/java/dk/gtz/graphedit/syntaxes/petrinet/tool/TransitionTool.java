package dk.gtz.graphedit.syntaxes.petrinet.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.tool.IToolbox;
import javafx.scene.Node;

public class TransitionTool extends ToolSelectorTool {
    public TransitionTool(IToolbox parent) {
	super(parent, "vertices");
    }

    @Override
    public Node getGraphic() {
	return new FontIcon(BootstrapIcons.SQUARE_FILL);
    }

    @Override
    public String getHelpDescription() {
	return """
	    Create new petrinet transition vertices
	    """;
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("Create transition vertices");
    }
}

