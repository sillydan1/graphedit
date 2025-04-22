package dk.gtz.graphedit.spi;

import java.util.Collection;
import java.util.List;

import dk.yalibs.yadi.DI;

/**
 * Base interface for a third-party plugin.
 * Each plugin must implement this to be discoverable by the plugin loader.
 */
public interface IPlugin {
	/**
	 * Get the name of this plugin.
	 * Should try to be unique
	 * 
	 * @return The name of this plugin implementation
	 */
	String getName();

	/**
	 * Get a general description of this plugin
	 * 
	 * @return A description of what kinds of utilities this plugin provides
	 */
	default String getDescription() {
		return "";
	}

	/**
	 * Event called when the plugin is initialized (before any syntax factories or
	 * panels are requested)
	 * At this point, only some things are registered in {@link DI}.
	 */
	default void onInitialize() {

	}

	/**
	 * Event called when the application has loaded.
	 * At this point, most things are registered in {@link DI}.
	 */
	default void onStart() {

	}

	/**
	 * Event called when the plugin is disabled by the user.
	 * Use this to clean up any processed or resources that has been loaded.
	 */
	default void onDestroy() {

	}

	/**
	 * Get a collection of syntax factories provided by this plugin. Will return an
	 * empty list by default.
	 * 
	 * @return A collection of syntax factories
	 * @throws Exception Allowed to throw any kind of exception. See the specific
	 *                   plugin implementation for details
	 */
	default Collection<ISyntaxFactory> getSyntaxFactories() throws Exception {
		return List.of();
	}

	/**
	 * Get a collection of panels provided by this plugin. Will return an empty list
	 * by default.
	 * 
	 * @return A collection of plugin panels
	 * @throws Exception Allowed to throw any kind of exception. See the specific
	 *                   plugin implementation for details
	 */
	default Collection<IPluginPanel> getPanels() throws Exception {
		return List.of();
	}

	/**
	 * Get a collection of language servers provided by this plugin. Will return an
	 * empty list by default.
	 * 
	 * @return A collection of language servers
	 * @throws Exception Allowed to throw any kind of exception. See the specific
	 *                   plugin implementation for details
	 */
	default Collection<ILanguageServer> getLanguageServers() throws Exception {
		return List.of();
	}

	/**
	 * Get a collection of importers provided by this plugin. Will return an empty
	 * list by default.
	 * 
	 * @return A collection of importers
	 */
	default Collection<IImporter> getImporters() {
		return List.of();
	}

	/**
	 * Get a collection of exporters provided by this plugin. Will return an empty
	 * list by default.
	 * 
	 * @return A collection of exporters
	 */
	default Collection<IExporter> getExporters() {
		return List.of();
	}
}
