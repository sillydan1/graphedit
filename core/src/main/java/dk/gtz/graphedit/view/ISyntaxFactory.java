package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.scene.Node;

public interface ISyntaxFactory {
    String getSyntaxName();
    Node createVertex(UUID vertexKey, ViewModelVertex vertexValue, ModelEditorController creatorController);
    Node createEdge(UUID edgeKey, ViewModelEdge edgeValue, ModelEditorController creatorController);
}

