package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.events.VertexMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Affine;

public class ModelEditorController extends BorderPane {
    private static Logger logger = (Logger)LoggerFactory.getLogger(ModelEditorController.class);
    private final ViewModelProjectResource resource;
    private StackPane viewport;
    private ModelEditorToolbar toolbar;
    private Group drawGroup;
    private Affine drawGroupTransform;
    private ObjectProperty<ITool> selectedTool;

    public ModelEditorController(ViewModelProjectResource resource) {
	this.resource = resource;
	this.selectedTool = new SimpleObjectProperty<>();
	initialize();
    }

    private void initialize() {
	initializeViewport();
	initializeToolbar();
	initializeDrawGroup();
	initializeToolEventHandlers();
	// TODO: Subscribe to changes in syntax().vertices map
	// TODO: Subscribe to changes in syntax().edges map
    }

    private void initializeViewport() {
	viewport = new StackPane();
	setCenter(viewport);
    }

    private void initializeToolbar() {
	var toolbox = DI.get(IToolbox.class);
	toolbar = new ModelEditorToolbar(toolbox, selectedTool);
	// TODO: experiment if having a tool-selection per viewport gets annoying. If it is, move the selectedTool variable into the toolbox itself then
	selectedTool.set(toolbox.getDefaultTool());
	setRight(toolbar);
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
	viewport.getChildren().add(drawPane);

	var gridPane = new GridPane(20.0); // TODO: gridsize should be adjustable
	gridPane.onChange(drawGroupTransform);
	viewport.getChildren().add(gridPane);
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
	    nodes.add(new VertexController(vertex.getKey(), vertex.getValue(), drawGroupTransform, selectedTool));
	return nodes;
    }

    private List<Node> initializeEdges() {
	var nodes = new ArrayList<Node>();
	for(var edge : resource.syntax().edges().entrySet())
	    nodes.add(new EdgeController(edge.getKey(), edge.getValue(), resource, drawGroupTransform, selectedTool));
	return nodes;
    }

    private void initializeToolEventHandlers() {
	viewport.addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onViewportMouseEvent(e));
    }
}

