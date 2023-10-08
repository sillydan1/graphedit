package dk.gtz.graphedit.syntaxes.text;

import java.util.UUID;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.syntaxes.text.view.TextVertexController;
import dk.gtz.graphedit.syntaxes.text.viewmodel.ViewModelTextVertex;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.EdgeController;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.view.ModelEditorController;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;

public class TextSyntaxFactory implements ISyntaxFactory {
	@Override public String getSyntaxName() {
		return "text";
	}

	@Override
	public String getSyntaxDescription() {
		return """
			A basic syntax where vertices and edges can be optionally labelled with a string of plaintext
			""";
	}

	@Override
	public Node createVertex(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController) {
		var toolbox = DI.get(IToolbox.class);
		var vertex = new ViewModelTextVertex(vertexValue);
		if(vertexValue instanceof ViewModelTextVertex textVertexValue)
			vertex = textVertexValue;
		return new TextVertexController(vertexKey, vertex, 
			creatorController.getViewportTransform(),
			creatorController.getProjectResource().syntax(),
			creatorController.getEditorSettings(),
			toolbox.getSelectedTool(),
			this);
	}

	@Override
	public Node createEdge(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController) {
		var toolbox = DI.get(IToolbox.class);
		return new EdgeController(edgeKey, edgeValue,
			creatorController.getProjectResource(),
			creatorController.getViewportTransform(),
			creatorController.getEditorSettings(),
			toolbox.getSelectedTool(),
			this);
	}

	@Override
	public ViewModelVertex createVertex(ModelVertex vertexValue) {
		return new ViewModelTextVertex(vertexValue);
	}

	@Override
	public ViewModelEdge createEdge(ModelEdge edgeValue) {
		return new ViewModelEdge(edgeValue);
	}
}

