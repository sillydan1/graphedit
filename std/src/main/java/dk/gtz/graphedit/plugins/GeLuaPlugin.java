package dk.gtz.graphedit.plugins;

import java.util.List;

import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginPanel;

public class GeLuaPlugin implements IPlugin {
	@Override
	public String getName() {
		return "GeLuaPlugin";
	}

	@Override
	public List<IPluginPanel> getPanels() {
		return List.of(new GeLuaPluginPanel());
	}
}
