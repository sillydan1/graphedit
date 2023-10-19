package dk.gtz.graphedit.syntaxes.text.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.syntaxes.text.viewmodel.ViewModelTextVertex;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.view.VertexController;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

public class TextVertexController extends VertexController {
    private final Logger logger = LoggerFactory.getLogger(TextVertexController.class);
    private ViewModelTextVertex vertex;
    private StackPane graphicsStack;
    private Rectangle rectangleGraphic;
    private Label label;

    public TextVertexController(UUID vertexKey, ViewModelTextVertex vertex, Affine viewportAffine, ViewModelGraph graph, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory) {
	super(vertexKey, vertex, viewportAffine, graph, editorSettings, selectedTool, syntaxFactory);
	this.vertex = vertex;
	this.label = createLabelGraphic();
	this.rectangleGraphic = createRectangleGraphic();
	initializeTextShapeShifter();
	initializeEventHandlers();
	resizeRectangle(vertex.getTextProperty().get());
    }

    private Label createLabelGraphic() {
	var label = new Label(vertex.getTextProperty().get());
	vertex.getTextProperty().bindBidirectional(label.textProperty());
	label.getStyleClass().add("outline");
	return label;
    }

    private Rectangle createRectangleGraphic() {
	var result = new Rectangle(40,40);
	result.getStyleClass().add("vertex-node");
	result.setArcWidth(8);
	result.setArcHeight(8);
	vertex.getTextProperty().addListener((e,o,n)-> resizeRectangle(n));
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> result.getStyleClass().add("stroke-hover"));
	addEventHandler(MouseEvent.MOUSE_EXITED,  event -> result.getStyleClass().remove("stroke-hover"));
	return result;
    }

    private void resizeRectangle(String newVal) {
	var t = new Text(newVal);
	t.setFont(label.getFont());
	var pane = new StackPane(t);
	pane.layout();
	var height = t.getLayoutBounds().getHeight();
	var width = t.getLayoutBounds().getWidth();
	var hpadding = 15;
	var wpadding = 18;
	rectangleGraphic.setHeight(height + hpadding);
	rectangleGraphic.setWidth(width + wpadding);
    }

    private void initializeTextShapeShifter() {
	this.graphicsStack = new StackPane();
	getChildren().add(graphicsStack);

	if(this.vertex.getTextProperty().get().isBlank())
	    setCircleGraphic();
	else
	    setRectangleGraphic();

	this.vertex.getTextProperty().addListener((e,o,n) -> {
	    if(n.isBlank())
		setCircleGraphic();
	    else
		setRectangleGraphic();
	});
    }

    private void initializeEventHandlers() {
	vertexValue.getIsSelected().addListener((e,o,n) -> {
	    if(n)
		rectangleGraphic.getStyleClass().add("stroke-selected");
	    else
		rectangleGraphic.getStyleClass().remove("stroke-selected");
	});
    }

    @Override
    protected void addLabel() {
	// Do nothing
    }

    @Override
    protected void addGraphic() {
	// Do nothing
    }

    private void setCircleGraphic() {
	graphicsStack.getChildren().clear();
	graphicsStack.getChildren().add(graphic);

	vertexValue.shape().shapeType().set(ViewModelShapeType.OVAL);
	vertexValue.shape().widthProperty().unbind();
	vertexValue.shape().heightProperty().unbind();
	vertexValue.shape().widthProperty().set(graphic.getRadius());
	vertexValue.shape().heightProperty().set(graphic.getRadius());
    }

    private void setRectangleGraphic() {
	graphicsStack.getChildren().clear();
	graphicsStack.getChildren().add(rectangleGraphic);
	graphicsStack.getChildren().add(label);
	resizeRectangle(vertex.getTextProperty().get());

	vertexValue.shape().shapeType().set(ViewModelShapeType.RECTANGLE);
	vertexValue.shape().widthProperty().unbind();
	vertexValue.shape().heightProperty().unbind();
	vertexValue.shape().widthProperty().bind(rectangleGraphic.widthProperty());
	vertexValue.shape().heightProperty().bind(rectangleGraphic.heightProperty());
    }
}

