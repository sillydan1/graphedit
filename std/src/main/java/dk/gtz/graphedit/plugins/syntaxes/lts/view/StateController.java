package dk.gtz.graphedit.plugins.syntaxes.lts.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel.ViewModelState;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.tool.ITool;
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

public class StateController extends VertexController {
    private final Logger logger = LoggerFactory.getLogger(StateController.class);
    private ViewModelState vertex;
    private StackPane graphicsStack;
    private Label label;
    private Rectangle rectangleGraphic;
    private Rectangle outerRectangleGraphic;

    public StateController(UUID vertexKey, ViewModelState vertex, Affine viewportAffine, ViewModelGraph graph, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory) {
        super(vertexKey, vertex, viewportAffine, graph, editorSettings, selectedTool, syntaxFactory);
        this.vertex = vertex;
        this.label = createLabelGraphic();
        this.graphicsStack = new StackPane();
        this.rectangleGraphic = createRectangleGraphic();
        this.outerRectangleGraphic = createRectangleGraphic();
        outerRectangleGraphic.visibleProperty().bind(vertex.initial());
        initializeEventHandlers();
        resizeRectangle(vertex.name().get());
        getChildren().add(graphicsStack);
        setRectangleGraphic();
    }

    private void initializeEventHandlers() {
        vertexValue.getIsSelected().addListener((e,o,n) -> {
            if(n) {
                rectangleGraphic.getStyleClass().add("stroke-selected");
                outerRectangleGraphic.getStyleClass().add("stroke-selected");
            } else {
                rectangleGraphic.getStyleClass().remove("stroke-selected");
                outerRectangleGraphic.getStyleClass().remove("stroke-selected");
            }
        });
    }

    private Label createLabelGraphic() {
        var label = new Label(vertex.name().get());
        vertex.name().bindBidirectional(label.textProperty());
        label.getStyleClass().add("outline");
        return label;
    }

    private Rectangle createRectangleGraphic() {
        var result = new Rectangle(40,40);
        result.getStyleClass().add("vertex-node");
        result.setArcWidth(8);
        result.setArcHeight(8);
        vertex.name().addListener((e,o,n)-> resizeRectangle(n));
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
        outerRectangleGraphic.setHeight(height + (hpadding * 1.5));
        outerRectangleGraphic.setWidth(width + (wpadding * 1.5));
        outerRectangleGraphic.setArcWidth(rectangleGraphic.getArcWidth() + ((wpadding * 1.5) - wpadding));
        outerRectangleGraphic.setArcHeight(rectangleGraphic.getArcHeight() + ((hpadding * 1.5) - hpadding));
    }

    private void setRectangleGraphic() {
        graphicsStack.getChildren().clear();
        graphicsStack.getChildren().add(outerRectangleGraphic);
        graphicsStack.getChildren().add(rectangleGraphic);
        graphicsStack.getChildren().add(label);
        resizeRectangle(vertex.name().get());

        vertexValue.shape().shapeType().set(ViewModelShapeType.RECTANGLE);
        vertexValue.shape().widthProperty().bind(rectangleGraphic.widthProperty());
        vertexValue.shape().heightProperty().bind(rectangleGraphic.heightProperty());
    }

    @Override
    protected void addLabel() {
        // Do nothing
    }

    @Override
    protected void addGraphic() {
        // Do nothing
    }
}

