package dk.gtz.graphedit.syntaxes.petrinet.view;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.syntaxes.petrinet.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.gtz.graphedit.view.VertexController;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.beans.property.ObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

public class TransitionController extends VertexController {
    private final Logger logger = LoggerFactory.getLogger(TransitionController.class);
    private final ViewModelTransition vertex;
    private final Rectangle rectangleGraphic;

    public TransitionController(UUID vertexKey, ViewModelTransition vertex, Affine viewportAffine, ViewModelGraph graph, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory) {
	super(vertexKey, vertex, viewportAffine, graph, editorSettings, selectedTool, syntaxFactory);
	this.vertex = vertex;
        this.rectangleGraphic = createRectangleGraphic();
        initializeEventHandlers();
	getChildren().add(rectangleGraphic);
    }

    @Override
    protected void addGraphic() {
	// do nothing
    }

    @Override
    protected void addLabel() {
	// do nothing
    }

    private void initializeEventHandlers() {
        vertex.getIsSelected().addListener((e,o,n) -> {
            if(n)
                rectangleGraphic.getStyleClass().add("stroke-selected");
            else
                rectangleGraphic.getStyleClass().remove("stroke-selected");
        });
    }

    private Rectangle createRectangleGraphic() {
        var result = new Rectangle(20,40);
        result.getStyleClass().add("vertex-node");
        result.setArcWidth(8);
        result.setArcHeight(8);
        addEventHandler(MouseEvent.MOUSE_ENTERED, event -> result.getStyleClass().add("stroke-hover"));
        addEventHandler(MouseEvent.MOUSE_EXITED,  event -> result.getStyleClass().remove("stroke-hover"));
        vertexValue.shape().widthProperty().bind(result.widthProperty());
        vertexValue.shape().heightProperty().bind(result.heightProperty());
        return result;
    }
}

