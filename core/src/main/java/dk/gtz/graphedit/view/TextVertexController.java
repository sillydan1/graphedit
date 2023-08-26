package dk.gtz.graphedit.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelTextVertex;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.transform.Affine;

public class TextVertexController extends VertexController {
    private final Logger logger = LoggerFactory.getLogger(TextVertexController.class);
    private final ViewModelTextVertex textVertex;
    private TextArea textArea;

    public TextVertexController(UUID vertexKey, ViewModelTextVertex vertex, Affine viewportAffine, ViewModelGraph graph, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool) {
	super(vertexKey, vertex, viewportAffine, graph, editorSettings, selectedTool);
	this.textVertex = vertex;
	textVertex.getTextProperty().bind(textArea.textProperty());
    }

    @Override
    protected Node initializeVertexRepresentation() {
	var textArea = new TextArea();
	vertexValue.shape().widthProperty().bind(textArea.widthProperty());
	vertexValue.shape().heightProperty().bind(textArea.heightProperty());
	textArea.getStyleClass().add("vertex-node");
	this.textArea = textArea;
	return textArea;
    }
}

