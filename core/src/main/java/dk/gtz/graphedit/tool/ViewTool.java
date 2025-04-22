package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.Node;

/**
 * The simplest tool. You can only view the model
 */
public class ViewTool extends AbstractBaseTool {
	/**
	 * Construct a new instance
	 */
	public ViewTool() {

	}

	@Override
	public String getHelpDescription() {
		return """
				Tool to view the model.

				Has no interaction events.
				""";
	}

	@Override
	public Optional<String> getTooltip() {
		return Optional.of("view the model");
	}

	@Override
	public Node getGraphic() {
		return new FontIcon(BootstrapIcons.EYE);
	}
}
