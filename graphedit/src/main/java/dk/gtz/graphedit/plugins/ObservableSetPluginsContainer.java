package dk.gtz.graphedit.plugins;

import java.util.List;
import java.util.Optional;

import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginsContainer;
import javafx.collections.ObservableSet;
import javafx.collections.FXCollections;

public class ObservableSetPluginsContainer implements IPluginsContainer {
	private final ObservableSet<IPlugin> plugins;

	public ObservableSetPluginsContainer() {
		plugins = FXCollections.observableSet();
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

	@Override
	public ObservableSet<IPlugin> getPlugins() {
		return plugins;
	}
}

