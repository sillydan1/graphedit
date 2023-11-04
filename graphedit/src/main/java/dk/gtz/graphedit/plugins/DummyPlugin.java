package dk.gtz.graphedit.plugins;

import java.util.List;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginPanel;
import javafx.scene.Node;
import javafx.scene.control.Label;

// TODO: Remove this class when you have extraced the other views into plugins
public class DummyPlugin implements IPlugin {
	public static class DummyPanel implements IPluginPanel {
		@Override
		public Node getIcon() {
			return new FontIcon(BootstrapIcons.ALARM);
		}

		@Override
		public Node getPanel() {
			return new Label("Hello from dummy plugin " + UUID.randomUUID().toString());
		}
	}

	@Override
	public String getName() {
		return "dummy";
	}

	@Override
	public List<IPluginPanel> getPanels() {
		return List.of(new DummyPanel());
	}
}

