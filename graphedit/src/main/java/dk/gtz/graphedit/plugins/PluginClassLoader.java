package dk.gtz.graphedit.plugins;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Loads a plugin leveraging a {@link URLClassLoader}. However, it restricts the plugin from
 * using the system classloader thereby trimming access to all system classes.
 *
 * Only the classes in SHARED_PACKAGES are visible to the plugin.
 */
public class PluginClassLoader extends URLClassLoader {
	private final ClassLoader parentClassLoader;
	public static final List<String> SHARED_PACKAGES = List.of(
			"dk.gtz.graphedit.spi",
			"dk.gtz.graphedit.view", // TODO: This should be removed
			"dk.gtz.graphedit.util",
			"dk.gtz.graphedit.events",
			"dk.gtz.graphedit.viewmodel",
			"dk.gtz.graphedit.model",
			"dk.gtz.graphedit.logging",
			"dk.gtz.graphedit.exceptions",
			"dk.gtz.graphedit.serialization",
			"dk.gtz.graphedit.tool",
			"dk.yalibs",
			"atlantafx",
			"javafx",
			"org.kordamp",
			"org.slf4j",
			"com.fasterxml.jackson"
	);

	public PluginClassLoader(URL[] urls, ClassLoader parentClassLoader) {
		super(urls, null);
		this.parentClassLoader = parentClassLoader;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		var loadedClass = findLoadedClass(name);
		if (loadedClass == null) {
			var isSharedClass = SHARED_PACKAGES.stream().anyMatch(name::startsWith);
			if (isSharedClass)
				loadedClass = parentClassLoader.loadClass(name);
			else
				loadedClass = super.loadClass(name, resolve);
		}

		if (resolve)
			resolveClass(loadedClass);
		return loadedClass;
	}
}

