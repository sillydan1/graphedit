package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelTextVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.Node;

public class DemoSyntaxFactory implements ISyntaxFactory {
    @Override
    public String getSyntaxName() {
	return "demo syntax";
    }

    @Override
    public Node createVertex(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController) {
	if(vertexValue instanceof ViewModelTextVertex textVertex)
	    return new TextVertexController(vertexKey, textVertex,
		    creatorController.getViewportTransform(),
		    creatorController.getProjectResource().syntax(),
		    creatorController.getEditorSettings(),
		    creatorController.getSelectedTool());

	return new VertexController(vertexKey, vertexValue, 
		creatorController.getViewportTransform(),
		creatorController.getProjectResource().syntax(),
		creatorController.getEditorSettings(),
		creatorController.getSelectedTool());
    }

    @Override
    public Node createEdge(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController) {
	return new EdgeController(edgeKey, edgeValue,
		creatorController.getProjectResource(),
		creatorController.getViewportTransform(),
		creatorController.getEditorSettings(),
		creatorController.getSelectedTool());
    }
}

