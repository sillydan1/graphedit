package dk.gtz.graphedit.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginsContainer;

public class ListPluginsContainer implements IPluginsContainer {
	private final List<IPlugin> plugins;

	public ListPluginsContainer() {
		plugins = new ArrayList<>();
	}

	@Override
	public IPluginsContainer add(IPlugin plugin) {
		plugins.add(plugin);
		return this;
	}

	@Override
	public IPluginsContainer add(List<IPlugin> plugins) {
		for(var plugin : plugins)
			this.plugins.add(plugin);
		return this;
	}

	@Override
	public IPluginsContainer add(IPlugin... plugins) {
		for(var plugin : plugins)
			this.plugins.add(plugin);
		return this;
	}

	@Override
	public IPluginsContainer remove(IPlugin plugin) {
		plugins.remove(plugin);
		return this;
	}

	@Override
	public Optional<IPlugin> get(String name) {
		for(var plugin : plugins)
			if(plugin.getName().equals(name))
				return Optional.of(plugin);
		return Optional.empty();
	}

	// TODO: This should be an observable list
	@Override
	public List<IPlugin> getPlugins() {
		return plugins;
	}
}

