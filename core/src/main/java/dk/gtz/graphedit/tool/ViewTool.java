package dk.gtz.graphedit.tool;

import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.view.events.VertexMouseEvent;
import javafx.scene.Node;

public class ViewTool extends AbstractBaseTool {
	@Override
	public Optional<String> getTooltip() {
        return Optional.of("View the model");
	}

	@Override
	public Node getGraphic() {
        return new FontIcon(BootstrapIcons.EYE);
	}

    @Override
    public void onVertexMouseEvent(VertexMouseEvent e) {
        // do nothing. we can only view
    }
}

