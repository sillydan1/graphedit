package dk.gtz.graphedit.plugins;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginsContainer;

public class PluginLoader {
	private static Logger logger = LoggerFactory.getLogger(PluginLoader.class);
	private final IPluginsContainer loadedPlugins;
	private final List<File> pluginsDirs;
	private final IModelSerializer serializer;

	public PluginLoader(List<String> pluginsDirs, IModelSerializer serializer) {
		this.pluginsDirs = new ArrayList<>(pluginsDirs.size());
		for(var pluginStr : pluginsDirs)
			this.pluginsDirs.add(new File(pluginStr));
		loadedPlugins = new ObservableSetPluginsContainer();
		this.serializer = serializer;
	}

	public IPluginsContainer getLoadedPlugins() {
		return loadedPlugins;
	}

	public PluginLoader loadPlugins() {
		loadedPlugins.clear();
		loadPlugin();
		for(var pluginsDir : pluginsDirs) {
			if(!pluginsDir.exists() || !pluginsDir.isDirectory()) {
				if(pluginsDir.toString().equals("plugins")) // Dont warn if the default plugins directory is not found
					continue;
				logger.warn("cannot load plugins, no such directory: '{}'", pluginsDir);
				continue;
			}

			logger.trace("looking for plugins in '{}'", pluginsDir.getAbsolutePath());
			var files = requireNonNull(pluginsDir.listFiles());
			for(var pluginDir : files)
				loadPlugin(pluginDir);
		}
		return this;
	}

	private void loadPlugin() {
		try {
			for(var plugin : ServiceLoader.load(IPlugin.class))
				loadPlugin(plugin);
		} catch(ServiceConfigurationError e) {
			logger.warn("failed to load plugin: {}", e.getMessage());
		}
	}

	private void loadPlugin(File pluginDir) {
		var currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			logger.trace("trying to load plugin: {}", pluginDir);
			var pluginClassLoader = createPluginClassLoader(pluginDir, currentClassLoader);
			Thread.currentThread().setContextClassLoader(pluginClassLoader);
			for(var plugin : ServiceLoader.load(IPlugin.class, pluginClassLoader))
				loadPlugin(plugin, pluginClassLoader);
		} catch(ServiceConfigurationError e) {
			logger.warn("failed to load plugin: {}", e.getMessage());
		} finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
	}

	private void loadPlugin(IPlugin plugin) {
		logger.trace("loaded plugin: {}", plugin.getName());
		loadedPlugins.add(plugin);
	}

	private void loadPlugin(IPlugin plugin, ClassLoader loader) {
		logger.trace("loaded plugin: {}", plugin.getName());
		loadedPlugins.add(plugin);
		serializer.addClassLoader(loader);
	}

	private URLClassLoader createPluginClassLoader(File file, ClassLoader loader) {
		var urls = Arrays.stream(Optional.ofNullable(file.listFiles()).orElse(new File[]{file}))
			.sorted()
			.map(File::toURI)
			.map(this::toUrl)
			.toArray(URL[]::new);
		return new PluginClassLoader(urls, loader);
	}

	private URL toUrl(URI uri) {
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
