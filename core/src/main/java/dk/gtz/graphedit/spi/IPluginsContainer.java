package dk.gtz.graphedit.spi;

import java.util.List;
import java.util.Optional;

public interface IPluginsContainer {
    IPluginsContainer add(IPlugin plugin);
    IPluginsContainer add(List<IPlugin> plugins);
    IPluginsContainer add(IPlugin... plugins);
    IPluginsContainer remove(IPlugin plugin);
    Optional<IPlugin> get(String name);
    List<IPlugin> getPlugins();
}

