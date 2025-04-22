package dk.gtz.graphedit.plugins.syntaxes.petrinet.view;

import java.util.UUID;

import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelArc;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.util.BindingsUtil;
import dk.gtz.graphedit.view.EdgeController;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.transform.Affine;

public class ArcController extends EdgeController {
	private final ViewModelArc edge;
	private Label label;
	private DoubleProperty labelDirOffset;

	public ArcController(UUID edgeKey, ViewModelArc edge, ViewModelGraph parentGraph, Affine viewportAffine,
			ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool,
			ISyntaxFactory syntaxFactory, String bufferKey) {
		super(edgeKey, edge, parentGraph, viewportAffine, editorSettings, selectedTool, syntaxFactory,
				bufferKey);
		this.edge = edge;
		this.labelDirOffset = new SimpleDoubleProperty(0.5);
		this.label = createWeightLabel();
		edge.weight().addListener((e, o, n) -> setLabelGraphic(this.label, n));
		getChildren().add(this.label);
	}

	private void setLabelGraphic(Label label, Number val) {
		if (val.intValue() == 1)
			label.textProperty().set("");
		else
			label.textProperty().set(val.toString());
	}

	private Label createWeightLabel() {
		var result = new Label();
		setLabelGraphic(result, edge.weight().get());
		result.getStyleClass().add("outline");
		result.translateXProperty().bind(BindingsUtil.createLineOffsetXBinding(line, labelDirOffset)
				.subtract(result.widthProperty().divide(2)));
		result.translateYProperty().bind(BindingsUtil.createLineOffsetYBinding(line, labelDirOffset)
				.subtract(result.heightProperty().divide(2)));
		return result;
	}
}
