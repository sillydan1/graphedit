package dk.gtz.graphedit.syntaxes.petrinet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.model.migration.ISyntaxMigrater;
import dk.gtz.graphedit.syntaxes.petrinet.model.ModelArc;
import dk.gtz.graphedit.syntaxes.petrinet.model.ModelPlace;
import dk.gtz.graphedit.syntaxes.petrinet.model.ModelTransition;
import dk.gtz.graphedit.syntaxes.petrinet.view.ArcController;
import dk.gtz.graphedit.syntaxes.petrinet.view.PlaceController;
import dk.gtz.graphedit.syntaxes.petrinet.view.TransitionController;
import dk.gtz.graphedit.syntaxes.petrinet.viewmodel.ViewModelArc;
import dk.gtz.graphedit.syntaxes.petrinet.viewmodel.ViewModelPlace;
import dk.gtz.graphedit.syntaxes.petrinet.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.view.ModelEditorController;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;

public class PNSyntaxFactory implements ISyntaxFactory {
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
            """;
    }

    @Override
    public Node createVertex(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController) {
		var toolbox = DI.get(IToolbox.class);
		if(vertexValue instanceof ViewModelPlace placeVertex)
            return new PlaceController(vertexKey, placeVertex,
                creatorController.getViewportTransform(),
                creatorController.getProjectResource().syntax(),
                creatorController.getEditorSettings(),
                toolbox.getSelectedTool(),
                this);
		if(vertexValue instanceof ViewModelTransition transitionVertex)
            return new TransitionController(vertexKey, transitionVertex,
                creatorController.getViewportTransform(),
                creatorController.getProjectResource().syntax(),
                creatorController.getEditorSettings(),
                toolbox.getSelectedTool(),
                this);
        throw new RuntimeException("not a petrinet vertex: %s".formatted(vertexValue.getClass().getName()));
    }

    @Override
    public ViewModelVertex createVertex(ModelVertex vertexValue) {
        if(vertexValue instanceof ModelPlace place)
            return new ViewModelPlace(place);
        if(vertexValue instanceof ModelTransition transition)
            return new ViewModelTransition(transition);
        throw new RuntimeException("not a petrinet vertex: %s".formatted(vertexValue.getClass().getName()));
    }

    @Override
    public Node createEdge(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController) {
		var toolbox = DI.get(IToolbox.class);
        if(edgeValue instanceof ViewModelArc arc)
            return new ArcController(edgeKey, arc,
                creatorController.getProjectResource(),
                creatorController.getViewportTransform(),
                creatorController.getEditorSettings(),
                toolbox.getSelectedTool(),
                this);
        throw new RuntimeException("not a petrinet edge: %s".formatted(edgeValue.getClass().getName()));
    }

    @Override
    public ViewModelEdge createEdge(ModelEdge edgeValue) {
        if(edgeValue instanceof ModelArc arc)
            return new ViewModelArc(arc);
        throw new RuntimeException("not a petrinet edge: %s".formatted(edgeValue.getClass().getName()));
    }

    @Override
    public Optional<ISyntaxMigrater> getMigrater() {
        return Optional.empty();
    }
}

