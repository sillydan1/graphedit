package dk.gtz.graphedit.plugins.syntaxes.petrinet.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelArc;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.EdgeController;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.view.util.BindingsUtil;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.transform.Affine;

public class ArcController extends EdgeController {
    private final Logger logger = LoggerFactory.getLogger(ArcController.class);
    private final ViewModelArc edge;
    private Label label;
    private DoubleProperty labelDirOffset;

    public ArcController(UUID edgeKey, ViewModelArc edge, ViewModelProjectResource resource, Affine viewportAffine, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory) {
        super(edgeKey, edge, resource, viewportAffine, editorSettings, selectedTool, syntaxFactory);
        this.edge = edge;
        this.labelDirOffset = new SimpleDoubleProperty(0.5);
        this.label = createWeightLabel();
        edge.weight().addListener((e,o,n) -> setLabelGraphic(this.label, n));
        getChildren().add(this.label);
    }

    private void setLabelGraphic(Label label, Number val) {
        if(val.intValue() == 1)
            label.textProperty().set("");
        else
            label.textProperty().set(val.toString());
    }

    private Label createWeightLabel() {
        var result = new Label();
        setLabelGraphic(result, edge.weight().get());
        result.getStyleClass().add("outline");
        result.translateXProperty().bind(BindingsUtil.getLineOffsetXBinding(
                    line.startXProperty(), line.startYProperty(),
                    line.endXProperty(), line.endYProperty(),
                    labelDirOffset).subtract(result.widthProperty().divide(2)));
        result.translateYProperty().bind(BindingsUtil.getLineOffsetYBinding(
                    line.startXProperty(), line.startYProperty(),
                    line.endXProperty(), line.endYProperty(),
                    labelDirOffset).subtract(result.heightProperty().divide(2)));
        return result;
    }
}

