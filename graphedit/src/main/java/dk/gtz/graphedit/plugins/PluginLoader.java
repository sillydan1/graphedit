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
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginsContainer;

public class PluginLoader {
	private static Logger logger = LoggerFactory.getLogger(PluginLoader.class);
	private final IPluginsContainer loadedPlugins;
	private final List<File> pluginsDirs;
	private final AtomicBoolean loading = new AtomicBoolean();

	public PluginLoader(List<String> pluginsDirs) {
		this.pluginsDirs = new ArrayList<>(pluginsDirs.size());
		for(var pluginStr : pluginsDirs)
			this.pluginsDirs.add(new File(pluginStr));
		loadedPlugins = new ListPluginsContainer();
	}

	public IPluginsContainer getLoadedPlugins() {
		return loadedPlugins;
	}

	public PluginLoader loadPlugins() {
		for(var pluginsDir : pluginsDirs) {
			logger.trace("looking for plugins in {}", pluginsDir.getAbsolutePath());
			if(!pluginsDir.exists() || !pluginsDir.isDirectory()) {
				logger.error("skipping plugin dir, no such file or directory {}", pluginsDir);
				continue;
			}

			if(loading.compareAndSet(false, true)) {
				var files = requireNonNull(pluginsDir.listFiles());
				for (var pluginDir : files)
					loadPlugin(pluginDir);
			}
		}
		return this;
	}

	private void loadPlugin(File pluginDir) {
		var currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			logger.trace("trying to load plugin: {}", pluginDir);
			var pluginClassLoader = createPluginClassLoader(pluginDir);
			Thread.currentThread().setContextClassLoader(pluginClassLoader);
			for(var plugin : ServiceLoader.load(IPlugin.class, pluginClassLoader))
				loadPlugin(plugin);
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

	private URLClassLoader createPluginClassLoader(File file) {
		var urls = Arrays.stream(Optional.ofNullable(file.listFiles()).orElse(new File[]{file}))
			.sorted()
			.map(File::toURI)
			.map(this::toUrl)
			.toArray(URL[]::new);
		return new PluginClassLoader(urls, getClass().getClassLoader());
	}

	private URL toUrl(URI uri) {
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}


