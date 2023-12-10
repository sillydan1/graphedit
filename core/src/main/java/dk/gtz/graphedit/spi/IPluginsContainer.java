package dk.gtz.graphedit.spi;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface for a container of {@link IPlugin}s
 * Can be used as a builder-pattern.
 */
public interface IPluginsContainer {
    /**
     * Add a single plugin to the container
     * @param plugin The plugin to add
     * @return A builder-pattern style reference to this
     */
    IPluginsContainer add(IPlugin plugin);

    /**
     * Add a list of plugins to the container
     * @param plugins A list of plugins to add
     * @return A builder-pattern style reference to this
     */
    IPluginsContainer add(List<IPlugin> plugins);

    /**
     * Add a list of plugins to the container
     * @param plugins Varargs of plugins to add
     * @return A builder-pattern style reference to this
     */
    IPluginsContainer add(IPlugin... plugins);

    /**
     * Remove a plugin from the container
     * @param plugin The plugin to remove
     * @return A builder-pattern style reference to this
     */
    IPluginsContainer remove(IPlugin plugin);

    /**
     * Get a plugin from the container based on the plugin name
     * @param name The {@link IPlugin#getName} to look for
     * @return The {@link IPlugin} instance if the plugin is present, empty otherwise
     */
    Optional<IPlugin> get(String name);

    /**
     * Get the underlying collection of plugins
     * @return The underlying collection of plugins
     */
    Collection<IPlugin> getPlugins();

    /**
     * Get a collection of plugins filtered such that it only contains the enabled plugins
     * @return A collection of the enabled plugins
     */
    Collection<IPlugin> getEnabledPlugins();
}
