package dk.gtz.graphedit.plugins.syntaxes.lts.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.util.BindingsUtil;
import dk.gtz.graphedit.view.EdgeController;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.transform.Affine;

public class TransitionController extends EdgeController {
    private final Logger logger = LoggerFactory.getLogger(TransitionController.class);
    private ViewModelTransition edge;
    private Label label;
    private DoubleProperty labelDirOffset;

    public TransitionController(UUID edgeKey, ViewModelTransition edge, ViewModelProjectResource resource, Affine viewportAffine, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory) {
        super(edgeKey, edge, resource, viewportAffine, editorSettings, selectedTool, syntaxFactory);
        this.edge = edge;
        this.labelDirOffset = new SimpleDoubleProperty(0.5);
        this.label = createActionLabel();
        this.label.textProperty().bind(edge.action());
        getChildren().add(label);
    }

    private Label createActionLabel() {
        var result = new Label(edge.action().get());
        result.getStyleClass().add("outline");
        result.translateXProperty().bind(BindingsUtil.createLineOffsetXBinding(line, labelDirOffset).subtract(result.widthProperty().divide(2)));
        result.translateYProperty().bind(BindingsUtil.createLineOffsetYBinding(line, labelDirOffset).subtract(result.heightProperty().divide(2)));
        return result;
    }
}

