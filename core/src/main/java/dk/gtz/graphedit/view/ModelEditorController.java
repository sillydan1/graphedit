package dk.gtz.graphedit.view;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.events.ViewportKeyEvent;
import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
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
    private final ViewModelEditorSettings settings;
    private StackPane viewport;
    private ModelEditorToolbar toolbar;
    private MapGroup<UUID> drawGroup;
    private Affine drawGroupTransform;
    private ObjectProperty<ITool> selectedTool;

    public ModelEditorController(ViewModelProjectResource resource) {
	this(resource, new ViewModelEditorSettings(20.0d, 20.0d, true));
    }

    public ModelEditorController(ViewModelProjectResource resource, ViewModelEditorSettings settings) {
	this.resource = resource;
	this.settings = settings;
	this.selectedTool = new SimpleObjectProperty<>();
	initialize();
    }

    private void initialize() {
	initializeViewport();
	initializeToolbar();
	initializeDrawGroup();
	initializeToolEventHandlers();
	initializeVertexCollectionChangeHandlers();
	initializeEdgeCollectionChangeHandlers();
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
	drawGroup = new MapGroup<>();
	drawGroupTransform = new Affine();
	drawGroup.addChildren(initializeEdges());
	drawGroup.addChildren(initializeLocations());
	drawGroup.getTransforms().add(drawGroupTransform);
	// TODO: move this into a seperate controller/fxml thingy

	var drawPane = new Pane(drawGroup.getGroup());
	drawPane.setOnScroll(this::onScrollingDrawPane);
	drawPane.setOnZoom(this::onZoomDrawPane);
	drawPane.prefWidthProperty().bind(widthProperty());
	drawPane.prefHeightProperty().bind(heightProperty());
	viewport.getChildren().add(drawPane);

	var gridPane = new GridPane(settings.gridSizeX().get(), settings.gridSizeY().get(), drawGroupTransform); // TODO: gridsize should be adjustable
	settings.gridSizeX().addListener((e,o,n) -> gridPane.setGridSize(n.doubleValue(), settings.gridSizeY().get()));
	settings.gridSizeY().addListener((e,o,n) -> gridPane.setGridSize(settings.gridSizeY().get(), n.doubleValue()));
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

    private Map<UUID,Node> initializeLocations() {
	var nodes = new HashMap<UUID,Node>();
	for(var vertex : resource.syntax().vertices().entrySet())
	    nodes.put(vertex.getKey(), createVertex(vertex.getKey(), vertex.getValue()));
	return nodes;
    }

    private Node createVertex(UUID vertexKey, ViewModelVertex vertexValue) {
	return new VertexController(vertexKey, vertexValue, drawGroupTransform, resource.syntax(), settings, selectedTool); // TODO: Should be an injectable factory pattern, so people can customize this
    }

    private Map<UUID,Node> initializeEdges() {
	var nodes = new HashMap<UUID,Node>();
	for(var edge : resource.syntax().edges().entrySet())
	    nodes.put(edge.getKey(), createEdge(edge.getKey(), edge.getValue()));
	return nodes;
    }

    private Node createEdge(UUID edgeKey, ViewModelEdge edgeValue) {
	return new EdgeController(edgeKey, edgeValue, resource, drawGroupTransform, settings, selectedTool);
    }

    private void initializeToolEventHandlers() {
	viewport.addEventHandler(MouseEvent.ANY, e -> selectedTool.get().onViewportMouseEvent(new ViewportMouseEvent(e, drawGroupTransform, resource.syntax(), settings)));
	Platform.runLater(() -> getScene().addEventHandler(KeyEvent.ANY, e -> selectedTool.get().onKeyEvent(new ViewportKeyEvent(e, drawGroupTransform, resource.syntax(), settings))));
    }

    private void initializeVertexCollectionChangeHandlers() {
	resource.syntax().vertices().addListener((MapChangeListener<UUID,ViewModelVertex>)c -> {
	    if(c.wasAdded())
		drawGroup.addChild(c.getKey(), createVertex(c.getKey(), c.getValueAdded()));
	    if(c.wasRemoved())
		drawGroup.removeChild(c.getKey());
	});
    }

    private void initializeEdgeCollectionChangeHandlers() {
	resource.syntax().edges().addListener((MapChangeListener<UUID,ViewModelEdge>)c -> {
	    if(c.wasAdded())
		drawGroup.addChild(c.getKey(), createEdge(c.getKey(), c.getValueAdded()));
	    if(c.wasRemoved())
		drawGroup.removeChild(c.getKey());
	});
    }
}

