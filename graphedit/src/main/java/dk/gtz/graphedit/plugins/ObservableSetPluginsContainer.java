package dk.gtz.graphedit.plugins;

import java.util.List;
import java.util.Optional;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.collections.ObservableSet;
import javafx.collections.FXCollections;

public class ObservableSetPluginsContainer implements IPluginsContainer {
	private final ObservableSet<IPlugin> plugins;
	private final ViewModelEditorSettings settings;

	public ObservableSetPluginsContainer() {
		plugins = FXCollections.observableSet();
		settings = DI.get(ViewModelEditorSettings.class);
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

	@Override
	public List<IPlugin> getEnabledPlugins() {
		return plugins.stream().filter(p -> !settings.disabledPlugins().contains(p.getName())).toList();
	}

	@Override
	public void clear() {
		plugins.clear();
	}
}
