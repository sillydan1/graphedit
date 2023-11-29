package dk.gtz.graphedit.plugins;

import java.util.Collection;
import java.util.List;

import dk.gtz.graphedit.plugins.syntaxes.lts.LTSSyntaxFactory;
import dk.gtz.graphedit.plugins.syntaxes.lts.lsp.LTSLanguageServer;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.PNSyntaxFactory;
import dk.gtz.graphedit.plugins.syntaxes.text.TextSyntaxFactory;
import dk.gtz.graphedit.spi.ILanguageServer;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginPanel;
import dk.gtz.graphedit.spi.ISyntaxFactory;

public class StandardPlugin implements IPlugin {
    @Override
    public String getName() {
        return "Standard";
    }

    @Override
    public List<ISyntaxFactory> getSyntaxFactories() throws Exception {
        return List.of(
                new LTSSyntaxFactory(),
                new PNSyntaxFactory(),
                new TextSyntaxFactory());
    }

    @Override
    public List<IPluginPanel> getPanels() throws Exception {
        return List.of(
                new ProjectFilesViewPanel(),
                new InspectorPanel(),
                new LintPanel());
    }

    @Override
    public Collection<ILanguageServer> getLanguageServers() throws Exception {
        return List.of(
                new LTSLanguageServer());
    }
}
