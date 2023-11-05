package dk.gtz.graphedit.plugins.syntaxes.petrinet.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelPlace;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.view.VertexController;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.transform.Affine;

public class PlaceController extends VertexController {
    private final Logger logger = LoggerFactory.getLogger(PlaceController.class);
    private final ViewModelPlace vertex;

    public PlaceController(UUID vertexKey, ViewModelPlace vertex, Affine viewportAffine, ViewModelGraph graph, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory) {
        super(vertexKey, vertex, viewportAffine, graph, editorSettings, selectedTool, syntaxFactory);
        this.vertex = vertex;
        getChildren().add(createLabelGraphic());
    }

    @Override
    protected void addLabel() {
        // do nothing
    }

    private Label createLabelGraphic() {
        var label = new Label();
        setLabelGraphic(label, vertex.initialTokenCount().get());
        vertex.initialTokenCount().addListener((e,o,n) -> setLabelGraphic(label, n));
        label.getStyleClass().add("outline");
        return label;
    }

    private void setLabelGraphic(Label label, Number val) {
        if(val.intValue() == 0)
            label.textProperty().set("");
        else
            label.textProperty().set("#"+val.toString());
    }
}
