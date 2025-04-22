package dk.gtz.graphedit.plugins.syntaxes.petrinet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelArc;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelPlace;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelTransition;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.tool.PlaceTool;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.tool.TransitionTool;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.view.ArcController;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.view.PlaceController;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.view.TransitionController;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelArc;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelPlace;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxMigrater;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.tool.Toolbox;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

public class PNSyntaxFactory implements ISyntaxFactory {
	private final IToolbox toolbox;

	public PNSyntaxFactory() {
		toolbox = new Toolbox("vertices", PlaceTool::new);
		toolbox.add(new TransitionTool(toolbox));
	}

	@Override
	public String getSyntaxName() {
		return "Petrinet";
	}

	@Override
	public List<String> getLegacyNames() {
		return List.of();
	}

	@Override
	public String getSyntaxDescription() {
		return """
				A Petri net consists of places, transitions, and arcs.
				Arcs run from a place to a transition or vice versa, never between places or between transitions.
				""";
	}

	@Override
	public Node createVertexView(String bufferKey, UUID vertexKey, ViewModelVertex vertexValue,
			ViewModelGraph graph, Affine viewportTransform) {
		var toolbox = DI.get(IToolbox.class);
		if (vertexValue instanceof ViewModelPlace placeVertex)
			return new PlaceController(vertexKey, placeVertex,
					viewportTransform,
					graph,
					DI.get(ViewModelEditorSettings.class),
					toolbox.getSelectedTool(),
					this, bufferKey);
		if (vertexValue instanceof ViewModelTransition transitionVertex)
			return new TransitionController(vertexKey, transitionVertex,
					viewportTransform,
					graph,
					DI.get(ViewModelEditorSettings.class),
					toolbox.getSelectedTool(),
					this, bufferKey);

		if (this.toolbox.getSelectedTool().get() instanceof PlaceTool)
			return new PlaceController(vertexKey, new ViewModelPlace(vertexKey, vertexValue),
					viewportTransform,
					graph,
					DI.get(ViewModelEditorSettings.class),
					toolbox.getSelectedTool(),
					this, bufferKey);
		if (this.toolbox.getSelectedTool().get() instanceof TransitionTool)
			return new TransitionController(vertexKey,
					new ViewModelTransition(vertexKey, vertexValue.toModel()),
					viewportTransform,
					graph,
					DI.get(ViewModelEditorSettings.class),
					toolbox.getSelectedTool(),
					this, bufferKey);

		throw new RuntimeException("not a petrinet vertex: %s".formatted(vertexValue.getClass().getName()));
	}

	@Override
	public ViewModelVertex createVertexViewModel(UUID vertexKey, ModelVertex vertexValue) {
		if (vertexValue instanceof ModelPlace place)
			return new ViewModelPlace(vertexKey, place);
		if (vertexValue instanceof ModelTransition transition)
			return new ViewModelTransition(vertexKey, transition);

		if (toolbox.getSelectedTool().get() instanceof PlaceTool)
			return new ViewModelPlace(vertexKey, vertexValue);
		if (toolbox.getSelectedTool().get() instanceof TransitionTool)
			return new ViewModelTransition(vertexKey, vertexValue);

		throw new RuntimeException("not a petrinet vertex: %s".formatted(vertexValue.getClass().getName()));
	}

	@Override
	public Node createEdgeView(String bufferKey, UUID edgeKey, ViewModelEdge edgeValue, ViewModelGraph graph,
			Affine viewportTransform) {
		var toolbox = DI.get(IToolbox.class);
		var arc = new ViewModelArc(edgeKey, edgeValue.toModel());
		if (edgeValue instanceof ViewModelArc tarc)
			arc = tarc;
		return new ArcController(edgeKey, arc,
				graph,
				viewportTransform,
				DI.get(ViewModelEditorSettings.class),
				toolbox.getSelectedTool(),
				this, bufferKey);
	}

	@Override
	public ViewModelEdge createEdgeViewModel(UUID edgeKey, ModelEdge edgeValue) {
		if (edgeValue instanceof ModelArc arc)
			return new ViewModelArc(edgeKey, arc);
		return new ViewModelArc(edgeKey, edgeValue);
	}

	@Override
	public Optional<ISyntaxMigrater> getMigrater() {
		return Optional.of(new PNSyntaxMigrater());
	}

	@Override
	public Optional<IToolbox> getSyntaxTools() {
		return Optional.of(toolbox);
	}
}
