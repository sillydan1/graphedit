package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;

public class ModelEditorController extends StackPane {
    private static Logger logger = (Logger)LoggerFactory.getLogger(ModelEditorController.class);
    private final ViewModelProjectResource resource;
    private Group drawGroup;
    private Affine drawGroupTransform;
    private Map<UUID, Node> vertices;
    private Map<UUID, Node> edges;

    public ModelEditorController(ViewModelProjectResource resource) {
	this.resource = resource;
	vertices = new HashMap<>();
	edges = new HashMap<>();
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
	drawGroup.getChildren().addAll(initializeLocations());
	drawGroup.getChildren().addAll(initializeEdges());
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
	for(var vertex : resource.syntax().vertices().entrySet()) {
	    // TODO: move this into a seperate controller/fxml thingy
	    var vertexPresentation = new StackPane();
	    var point = vertex.getValue().position();
	    var circle = new Circle(20.0);
	    circle.strokeTypeProperty().set(StrokeType.INSIDE);
	    circle.getStyleClass().add("vertex-node");
	    vertexPresentation.getChildren().add(circle);

	    var label = new Label(vertex.getKey().toString());
	    label.getStyleClass().add("outline");

	    vertexPresentation.getChildren().add(label);
	    vertexPresentation.setTranslateX(point.get().x);
	    vertexPresentation.setTranslateY(point.get().y);
	    vertexPresentation.setCursor(Cursor.HAND);
	    vertexPresentation.getStyleClass().add("scale");

	    nodes.add(vertexPresentation);
	    vertices.put(vertex.getKey(), vertexPresentation);
	}
	return nodes;
    }

    private List<Node> initializeEdges() {
	var nodes = new ArrayList<Node>();
	for(var edge : resource.syntax().edges().entrySet()) {
	    var edgePresentation = new Line();
	    edgePresentation.setStroke(Color.WHITE);
	    var sourceVertex = vertices.get(edge.getValue().source().getValue());
	    var targetVertex = vertices.get(edge.getValue().target().getValue());
	    edgePresentation.startXProperty().bind(sourceVertex.translateXProperty());
	    edgePresentation.startYProperty().bind(sourceVertex.translateYProperty());
	    edgePresentation.endXProperty().bind(targetVertex.translateXProperty());
	    edgePresentation.endYProperty().bind(targetVertex.translateYProperty());
	    nodes.add(edgePresentation);
	    edges.put(edge.getKey(), edgePresentation);
	}
	return nodes;
    }
}

