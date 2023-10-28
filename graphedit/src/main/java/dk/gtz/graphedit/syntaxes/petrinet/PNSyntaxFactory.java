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
import dk.gtz.graphedit.syntaxes.petrinet.tool.PlaceTool;
import dk.gtz.graphedit.syntaxes.petrinet.tool.TransitionTool;
import dk.gtz.graphedit.syntaxes.petrinet.view.ArcController;
import dk.gtz.graphedit.syntaxes.petrinet.view.PlaceController;
import dk.gtz.graphedit.syntaxes.petrinet.view.TransitionController;
import dk.gtz.graphedit.syntaxes.petrinet.viewmodel.ViewModelArc;
import dk.gtz.graphedit.syntaxes.petrinet.viewmodel.ViewModelPlace;
import dk.gtz.graphedit.syntaxes.petrinet.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.tool.Toolbox;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.view.ModelEditorController;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;

public class PNSyntaxFactory implements ISyntaxFactory {
    private final IToolbox toolbox;

    public PNSyntaxFactory() {
        toolbox = new Toolbox("vertices", 
                new PlaceTool(),
                new TransitionTool());
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
            """;
    }

    @Override
    public Node createVertexView(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController) {
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

        if(this.toolbox.getSelectedTool().get() instanceof PlaceTool)
            return new PlaceController(vertexKey, new ViewModelPlace(vertexValue),
                    creatorController.getViewportTransform(),
                    creatorController.getProjectResource().syntax(),
                    creatorController.getEditorSettings(),
                    toolbox.getSelectedTool(),
                    this);
        if(this.toolbox.getSelectedTool().get() instanceof TransitionTool)
            return new TransitionController(vertexKey, new ViewModelTransition(vertexValue.toModel()),
                    creatorController.getViewportTransform(),
                    creatorController.getProjectResource().syntax(),
                    creatorController.getEditorSettings(),
                    toolbox.getSelectedTool(),
                    this);

        throw new RuntimeException("not a petrinet vertex: %s".formatted(vertexValue.getClass().getName()));
    }

    @Override
    public ViewModelVertex createVertexViewModel(ModelVertex vertexValue) {
        if(vertexValue instanceof ModelPlace place)
            return new ViewModelPlace(place);
        if(vertexValue instanceof ModelTransition transition)
            return new ViewModelTransition(transition);

        if(toolbox.getSelectedTool().get() instanceof PlaceTool)
            return new ViewModelPlace(vertexValue);
        if(toolbox.getSelectedTool().get() instanceof TransitionTool)
            return new ViewModelTransition(vertexValue);

        throw new RuntimeException("not a petrinet vertex: %s".formatted(vertexValue.getClass().getName()));
    }

    @Override
    public Node createEdgeView(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController) {
        var toolbox = DI.get(IToolbox.class);
        var arc = new ViewModelArc(edgeValue.toModel());
        if(edgeValue instanceof ViewModelArc tarc)
            arc = tarc;
        return new ArcController(edgeKey, arc,
                creatorController.getProjectResource(),
                creatorController.getViewportTransform(),
                creatorController.getEditorSettings(),
                toolbox.getSelectedTool(),
                this);
    }

    @Override
    public ViewModelEdge createEdgeViewModel(ModelEdge edgeValue) {
        if(edgeValue instanceof ModelArc arc)
            return new ViewModelArc(arc);
        return new ViewModelArc(edgeValue);
    }

    @Override
    public Optional<ISyntaxMigrater> getMigrater() {
        return Optional.empty();
    }

    @Override
    public Optional<IToolbox> getSyntaxTools() {
        return Optional.of(toolbox);
    }
}

