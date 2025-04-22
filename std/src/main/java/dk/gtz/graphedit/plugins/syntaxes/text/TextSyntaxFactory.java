package dk.gtz.graphedit.plugins.syntaxes.text;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.text.view.TextVertexController;
import dk.gtz.graphedit.plugins.syntaxes.text.viewmodel.ViewModelTextVertex;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxMigrater;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.EdgeController;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

public class TextSyntaxFactory implements ISyntaxFactory {
	@Override
	public String getSyntaxName() {
		return "Simple";
	}

	@Override
	public List<String> getLegacyNames() {
		return List.of("text");
	}

	@Override
	public String getSyntaxDescription() {
		return """
				A basic syntax where vertices and edges can be optionally labelled with a string of plaintext
				""";
	}

	@Override
	public Node createVertexView(String bufferKey, UUID vertexKey, ViewModelVertex vertexValue,
			ViewModelGraph graph, Affine viewportTransform) {
		var toolbox = DI.get(IToolbox.class);
		var vertex = new ViewModelTextVertex(vertexKey, vertexValue);
		if (vertexValue instanceof ViewModelTextVertex textVertexValue)
			vertex = textVertexValue;
		return new TextVertexController(vertexKey, vertex,
				viewportTransform,
				graph,
				DI.get(ViewModelEditorSettings.class),
				toolbox.getSelectedTool(),
				this, bufferKey);
	}

	@Override
	public Node createEdgeView(String bufferKey, UUID edgeKey, ViewModelEdge edgeValue, ViewModelGraph graph,
			Affine viewportTransform) {
		var toolbox = DI.get(IToolbox.class);
		return new EdgeController(edgeKey, edgeValue,
				graph,
				viewportTransform,
				DI.get(ViewModelEditorSettings.class),
				toolbox.getSelectedTool(),
				this, bufferKey);
	}

	@Override
	public ViewModelVertex createVertexViewModel(UUID vertexKey, ModelVertex vertexValue) {
		return new ViewModelTextVertex(vertexKey, vertexValue);
	}

	@Override
	public ViewModelEdge createEdgeViewModel(UUID edgeKey, ModelEdge edgeValue) {
		return new ViewModelEdge(edgeKey, edgeValue);
	}

	@Override
	public Optional<ISyntaxMigrater> getMigrater() {
		return Optional.of(new TextSyntaxMigrater());
	}

	@Override
	public Optional<IToolbox> getSyntaxTools() {
		return Optional.empty();
	}
}
