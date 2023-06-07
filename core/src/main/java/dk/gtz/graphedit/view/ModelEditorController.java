package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;

public class ModelEditorController extends StackPane {
    private static Logger logger = (Logger)LoggerFactory.getLogger(ModelEditorController.class);
    private final ViewModelProjectResource resource;
    private Group drawGroup;
    private Affine drawGroupTransform;

    public ModelEditorController(ViewModelProjectResource resource) {
	this.resource = resource;
	initialize();
    }

    private void initialize() {
	initializeDrawGroup();
	// TODO: Subscribe to changes in syntax().vertices map
	// TODO: Subscribe to changes in syntax().edges map
    }

    private void initializeDrawGroup() {
	drawGroup = new Group();
	drawGroupTransform = new Affine();
	drawGroup.getChildren().addAll(initializeEdges());
	drawGroup.getChildren().addAll(initializeLocations());
	drawGroup.getTransforms().add(drawGroupTransform);
	// TODO: move this into a seperate controller/fxml thingy

	var drawPane = new Pane(drawGroup);
	drawPane.setOnScroll(this::onScrollingDrawPane);
	drawPane.setOnZoom(this::onZoomDrawPane);
	drawPane.prefWidthProperty().bind(widthProperty());
	drawPane.prefHeightProperty().bind(heightProperty());
	getChildren().add(drawPane);

	var gridPane = new GridPane(20.0); // TODO: gridsize should be adjustable
	gridPane.onChange(drawGroupTransform);
	getChildren().add(gridPane);
	gridPane.toBack();
    }

    private void onScrollingDrawPane(ScrollEvent event) {
	drawGroupTransform.appendTranslation(event.getDeltaX(), event.getDeltaY());
    }

    private void onZoomDrawPane(ZoomEvent event) {
	var centerX = (getWidth() * 0.5) - drawGroupTransform.getTx();
	var centerY = (getHeight() * 0.5) - drawGroupTransform.getTy();
	var adjustedCenterX = centerX / drawGroupTransform.getMxx();
	var adjustedCenterY = centerY / drawGroupTransform.getMyy();
	drawGroupTransform.appendScale(event.getZoomFactor(), event.getZoomFactor(), adjustedCenterX, adjustedCenterY);
    }

    private List<Node> initializeLocations() {
	var nodes = new ArrayList<Node>();
	for(var vertex : resource.syntax().vertices().entrySet())
	    nodes.add(new VertexController(vertex.getKey(), vertex.getValue(), drawGroupTransform));
	return nodes;
    }

    private List<Node> initializeEdges() {
	var nodes = new ArrayList<Node>();
	for(var edge : resource.syntax().edges().entrySet())
	    nodes.add(new EdgeController(edge.getKey(), edge.getValue(), resource));
	return nodes;
    }
}

