package dk.gtz.graphedit.spi;

import java.util.List;

import dk.gtz.graphedit.view.ISyntaxFactory;

public interface IPlugin {
    String getName();

    default List<ISyntaxFactory> getSyntaxFactories() throws Exception {
        return List.of();
    }

    default List<IPluginPanel> getPanels() throws Exception {
        return List.of();
    }
}

