package dk.gtz.graphedit.internal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxMigrater;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.EdgeController;
import dk.gtz.graphedit.view.VertexController;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * Syntax factory implementation for the default basic demonstration syntax
 */
public class DemoSyntaxFactory implements ISyntaxFactory {
    /**
     * Constructs a new instance of the demo syntaxfactory
     */
    public DemoSyntaxFactory() {

    }

    @Override
    public String getSyntaxName() {
	return "Demo";
    }

    @Override
    public List<String> getLegacyNames() {
	return List.of("demo syntax");
    }

    @Override
    public String getSyntaxDescription() {
	return """
	    A very basic syntax for demonstration purposes

	    not meant for actual projects.
	    """;
    }

    @Override
    public Node createVertexView(String bufferKey, UUID vertexKey, ViewModelVertex vertexValue, ViewModelGraph graph, Affine viewportTransform) {
	var toolbox = DI.get(IToolbox.class);
	return new VertexController(vertexKey, vertexValue, 
		viewportTransform,
		graph,
		DI.get(ViewModelEditorSettings.class),
		toolbox.getSelectedTool(),
		this, bufferKey);
    }

    @Override
    public Node createEdgeView(String bufferKey, UUID edgeKey, ViewModelEdge edgeValue, ViewModelGraph graph, Affine viewportTransform) {
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
	return new ViewModelVertex(vertexKey, vertexValue, new ViewModelVertexShape(ViewModelShapeType.OVAL));
    }

    @Override
    public ViewModelEdge createEdgeViewModel(UUID edgeKey, ModelEdge edgeValue) {
	return new ViewModelEdge(edgeKey, edgeValue);
    }

    @Override
    public Optional<ISyntaxMigrater> getMigrater() {
	return Optional.of(new DemoSyntaxMigrater());
    }

    @Override
    public Optional<IToolbox> getSyntaxTools() {
	return Optional.empty();
    }
}
