package dk.gtz.graphedit.plugins;

import java.util.List;

import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginPanel;
import dk.gtz.graphedit.view.ISyntaxFactory;

public class StandardPlugin implements IPlugin {
    @Override
    public String getName() {
        return "Standard";
    }

    @Override
    public List<ISyntaxFactory> getSyntaxFactories() {
        return List.of();
    }

    @Override
    public List<IPluginPanel> getPanels() {
        return List.of(new ProjectFilesViewPanel());
    }
}

