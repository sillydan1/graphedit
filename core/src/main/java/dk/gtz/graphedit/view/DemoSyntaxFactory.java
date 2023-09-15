package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelTextVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;

public class DemoSyntaxFactory implements ISyntaxFactory {
    @Override
    public String getSyntaxName() {
	return "demo syntax";
    }

    @Override
    public Node createVertex(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController) {
	var toolbox = DI.get(IToolbox.class);

	if(vertexValue instanceof ViewModelTextVertex textVertex)
	    return new TextVertexController(vertexKey, textVertex,
		    creatorController.getViewportTransform(),
		    creatorController.getProjectResource().syntax(),
		    creatorController.getEditorSettings(),
		    toolbox.getSelectedTool());

	return new VertexController(vertexKey, vertexValue, 
		creatorController.getViewportTransform(),
		creatorController.getProjectResource().syntax(),
		creatorController.getEditorSettings(),
		toolbox.getSelectedTool());
    }

    @Override
    public Node createEdge(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController) {
	var toolbox = DI.get(IToolbox.class);
	return new EdgeController(edgeKey, edgeValue,
		creatorController.getProjectResource(),
		creatorController.getViewportTransform(),
		creatorController.getEditorSettings(),
		toolbox.getSelectedTool());
    }
}

