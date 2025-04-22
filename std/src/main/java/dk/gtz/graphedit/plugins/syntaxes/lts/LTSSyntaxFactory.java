package dk.gtz.graphedit.plugins.syntaxes.lts;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.lts.view.StateController;
import dk.gtz.graphedit.plugins.syntaxes.lts.view.TransitionController;
import dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel.ViewModelState;
import dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxMigrater;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

public class LTSSyntaxFactory implements ISyntaxFactory {
	@Override
	public String getSyntaxName() {
		return "LTS";
	}

	@Override
	public List<String> getLegacyNames() {
		return List.of();
	}

	@Override
	public String getSyntaxDescription() {
		return """
				A labelled transition system LTS is a way of modelling concurrent systems.

				This syntax is very inspired by the definition provided in "Principles of Model Checking" by Christel Baier and Joost-Pieter Katoen.
				""";
	}

	@Override
	public Node createVertexView(String bufferKey, UUID vertexKey, ViewModelVertex vertexValue,
			ViewModelGraph graph, Affine viewportTransform) {
		var settings = DI.get(ViewModelEditorSettings.class);
		var toolbox = DI.get(IToolbox.class);
		var vertex = new ViewModelState(vertexKey, vertexValue);
		if (vertexValue instanceof ViewModelState tvertex)
			vertex = tvertex;
		return new StateController(vertexKey, vertex,
				viewportTransform,
				graph,
				settings,
				toolbox.getSelectedTool(),
				this, bufferKey);
	}

	@Override
	public ViewModelVertex createVertexViewModel(UUID vertexKey, ModelVertex vertexValue) {
		return new ViewModelState(vertexKey, vertexValue);
	}

	@Override
	public Node createEdgeView(String bufferKey, UUID edgeKey, ViewModelEdge edgeValue, ViewModelGraph graph,
			Affine viewportTransform) {
		var settings = DI.get(ViewModelEditorSettings.class);
		var toolbox = DI.get(IToolbox.class);
		var edge = new ViewModelTransition(edgeKey, edgeValue);
		if (edgeValue instanceof ViewModelTransition tedge)
			edge = tedge;
		return new TransitionController(edgeKey, edge,
				graph,
				viewportTransform,
				settings,
				toolbox.getSelectedTool(),
				this, bufferKey);
	}

	@Override
	public ViewModelEdge createEdgeViewModel(UUID edgeKey, ModelEdge edgeValue) {
		return new ViewModelTransition(edgeKey, edgeValue);
	}

	@Override
	public Optional<ISyntaxMigrater> getMigrater() {
		return Optional.of(new LTSSyntaxMigrater());
	}

	@Override
	public Optional<IToolbox> getSyntaxTools() {
		return Optional.empty();
	}
}
