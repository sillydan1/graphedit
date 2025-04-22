package dk.gtz.graphedit.plugins;

import java.util.Collection;
import java.util.List;

import dk.gtz.graphedit.plugins.syntaxes.lts.LTSSyntaxFactory;
import dk.gtz.graphedit.plugins.syntaxes.lts.lsp.LTSLanguageServer;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.PNSyntaxFactory;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal.TapaalPNMLExporter;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal.TapaalPNMLImporter;
import dk.gtz.graphedit.plugins.syntaxes.text.TextSyntaxFactory;
import dk.gtz.graphedit.spi.IExporter;
import dk.gtz.graphedit.spi.IImporter;
import dk.gtz.graphedit.spi.ILanguageServer;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.spi.IPluginPanel;
import dk.gtz.graphedit.spi.ISyntaxFactory;

public class StandardPlugin implements IPlugin {
	private final LTSSyntaxFactory lts;
	private final PNSyntaxFactory pn;
	private final TextSyntaxFactory text;

	public StandardPlugin() {
		this.lts = new LTSSyntaxFactory();
		this.pn = new PNSyntaxFactory();
		this.text = new TextSyntaxFactory();
	}

	@Override
	public String getName() {
		return "Standard";
	}

	@Override
	public String getDescription() {
		return """
				Standard plugin. Provides all the basics. Be careful disabling this.

				Syntaxes provided:
				    - LTS
				    - Petrinet
				    - Simple

				Language Servers provided:
				    - lts-ls (Example language server for LTS syntax)

				Panels provided:
				    - Files panel
				    - Syntax Element Property Editor panel
				    - Lint Inspector panel
				    - Plugin panel

				Importers provided:
				    - Tapaal PNML importer
				""";
	}

	@Override
	public List<ISyntaxFactory> getSyntaxFactories() throws Exception {
		return List.of(lts, pn, text);
	}

	@Override
	public List<IPluginPanel> getPanels() throws Exception {
		return List.of(
				new ProjectFilesViewPanel(),
				new InspectorPanel(),
				new LintPanel(),
				new PluginManagementPanel(),
				new UndoTreePanel());
	}

	@Override
	public Collection<ILanguageServer> getLanguageServers() throws Exception {
		return List.of(new LTSLanguageServer());
	}

	@Override
	public Collection<IImporter> getImporters() {
		return List.of(new TapaalPNMLImporter(pn.getSyntaxName()));
	}

	@Override
	public Collection<IExporter> getExporters() {
		return List.of(new TapaalPNMLExporter(pn.getSyntaxName()));
	}
}
