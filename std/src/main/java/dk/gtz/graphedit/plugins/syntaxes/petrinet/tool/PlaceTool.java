package dk.gtz.graphedit.plugins.syntaxes.petrinet.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.tool.IToolbox;
import javafx.scene.Node;

public class PlaceTool extends ToolSelectorTool {
	public PlaceTool(IToolbox parentToolbox) {
		super(parentToolbox, "vertices");
	}

	@Override
	public Node getGraphic() {
		return new FontIcon(BootstrapIcons.CIRCLE);
	}

	@Override
	public String getHelpDescription() {
		return """
				Create new petrinet place vertices
				""";
	}

	@Override
	public Optional<String> getTooltip() {
		return Optional.of("Create place vertices");
	}
}
