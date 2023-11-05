package dk.gtz.graphedit.plugins.syntaxes.lts;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.model.migration.ISyntaxMigrater;
import dk.gtz.graphedit.plugins.syntaxes.lts.view.StateController;
import dk.gtz.graphedit.plugins.syntaxes.lts.view.TransitionController;
import dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel.ViewModelState;
import dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.ModelEditorController;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;

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
    public Node createVertexView(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController) {
	var toolbox = DI.get(IToolbox.class);
	var vertex = new ViewModelState(vertexValue);
	if(vertexValue instanceof ViewModelState tvertex)
	    vertex = tvertex;
	return new StateController(vertexKey, vertex, 
		creatorController.getViewportTransform(),
		creatorController.getProjectResource().syntax(),
		creatorController.getEditorSettings(),
		toolbox.getSelectedTool(),
		this);
    }

    @Override
    public ViewModelVertex createVertexViewModel(ModelVertex vertexValue) {
	return new ViewModelState(vertexValue);
    }

    @Override
    public Node createEdgeView(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController) {
	var toolbox = DI.get(IToolbox.class);
	var edge = new ViewModelTransition(edgeValue);
	if(edgeValue instanceof ViewModelTransition tedge)
	    edge = tedge;
	return new TransitionController(edgeKey, edge,
		creatorController.getProjectResource(),
		creatorController.getViewportTransform(),
		creatorController.getEditorSettings(),
		toolbox.getSelectedTool(),
		this);
    }

    @Override
    public ViewModelEdge createEdgeViewModel(ModelEdge edgeValue) {
	return new ViewModelTransition(edgeValue);
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

