package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.view.events.ViewportKeyEvent;
import dk.gtz.graphedit.view.events.ViewportMouseEvent;
import dk.gtz.graphedit.view.util.MetadataUtils;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;

public class ModelEditorController extends BorderPane implements IFocusable {
    private static Logger logger = (Logger)LoggerFactory.getLogger(ModelEditorController.class);
    private double drawPaneDragStartX, drawPaneDragStartY;
    private final ViewModelProjectResource resource;
    private final ViewModelEditorSettings settings;
    private ISyntaxFactory syntaxFactory;
    private StackPane viewport;
    private ModelEditorToolbar toolbar;
    private MapGroup<UUID> drawGroup;
    private GridPane gridPane;
    private Pane drawPane;
    private Affine drawGroupTransform;
    private List<Runnable> onFocusEventHandlers;

    public ModelEditorController(ViewModelProjectResource resource, ISyntaxFactory syntaxFactory) {
	this(resource, DI.get(ViewModelEditorSettings.class), syntaxFactory);
    }

    public ModelEditorController(ViewModelProjectResource resource, ViewModelEditorSettings settings, ISyntaxFactory syntaxFactory) {
	this.resource = resource;
	this.settings = settings;
	this.syntaxFactory = syntaxFactory;
	this.onFocusEventHandlers = new ArrayList<>();
	initialize();
    }

    private void initialize() {
	initializeViewport();
	initializeToolbar();
	initializeDrawGroup();
	initializeMetadataEventHandlers();
	initializeVertexCollectionChangeHandlers();
	initializeEdgeCollectionChangeHandlers();
	initializeToolEventHandlers();
    }

    private void initializeViewport() {
	viewport = new StackPane();
	setCenter(viewport);
    }

    private void initializeToolbar() {
	var toolbox = DI.get(IToolbox.class);
	toolbar = new ModelEditorToolbar(toolbox, toolbox.getSelectedTool(), resource).withSyntaxSelector().withButtons();
	var top = new VBox(toolbar);
	var syntaxTools = syntaxFactory.getSyntaxTools();
	if(syntaxTools.isPresent())
	    top.getChildren().add(new ModelEditorToolbar(syntaxTools.get(), syntaxTools.get().getSelectedTool(), resource).withButtons());
	setTop(top);
    }

    private void initializeDrawGroup() {
	drawGroup = new MapGroup<>();
	drawGroupTransform = new Affine();
	drawGroup.addChildren(initializeEdges());
	drawGroup.addChildren(initializeVertices());
	drawGroup.getTransforms().add(drawGroupTransform);

	drawPane = new Pane(drawGroup.getGroup());
	drawPane.setOnScroll(this::onScrollingDrawPane);
	drawPane.setOnZoom(this::onZoomDrawPane);
	// TODO: make an abstraction called DeltaDragEvent or something
	drawPane.setOnMousePressed(this::onPressingDrawPane);
	drawPane.setOnMouseDragged(this::onDraggingDrawPane);
	drawPane.prefWidthProperty().bind(widthProperty());
	drawPane.prefHeightProperty().bind(heightProperty());
	viewport.getChildren().add(drawPane);

	gridPane = new GridPane(settings.gridSizeX(), settings.gridSizeY(), drawGroupTransform);
	settings.gridSizeX().addListener((e,o,n) -> gridPane.setGridSize(n.doubleValue(), settings.gridSizeY().get()));
	settings.gridSizeY().addListener((e,o,n) -> gridPane.setGridSize(settings.gridSizeY().get(), n.doubleValue()));
	viewport.getChildren().add(gridPane);
	gridPane.toBack();
    }

    private void onPressingDrawPane(MouseEvent event) {
	drawPaneDragStartX = event.getX();
	drawPaneDragStartY = event.getY();
    }

    private void onDraggingDrawPane(MouseEvent event) {
	if(!event.isSecondaryButtonDown()) // TODO: The button to press should be configurable
	    return;
	var dx = event.getX() - drawPaneDragStartX;
	var dy = event.getY() - drawPaneDragStartY;
	drawGroupTransform.appendTranslation(dx, dy);
	drawPaneDragStartX = event.getX();
	drawPaneDragStartY = event.getY();
    }

    private void onScrollingDrawPane(ScrollEvent event) {
	if(event.isControlDown())
	    zoomDrawPane(1 - (event.getDeltaY() * 0.01f)); // TODO: Consider having an adjustable scalar
	else
	    drawGroupTransform.appendTranslation(event.getDeltaX(), event.getDeltaY());
    }

    private void zoomDrawPane(double zoomFactor) {
	zoomDrawPane(zoomFactor, zoomFactor);
    }

    private void zoomDrawPane(double zoomFactorX, double zoomFactorY) {
	var centerX = (getWidth() * 0.5) - drawGroupTransform.getTx();
	var centerY = (getHeight() * 0.5) - drawGroupTransform.getTy();
	var adjustedCenterX = centerX / drawGroupTransform.getMxx();
	var adjustedCenterY = centerY / drawGroupTransform.getMyy();
	drawGroupTransform.appendScale(zoomFactorX, zoomFactorY, adjustedCenterX, adjustedCenterY);
    }

    private void onZoomDrawPane(ZoomEvent event) {
	zoomDrawPane(event.getZoomFactor());
    }

    private Map<UUID,Node> initializeVertices() {
	var nodes = new HashMap<UUID,Node>();
	for(var vertex : resource.syntax().vertices().entrySet()) {
	    nodes.put(vertex.getKey(), syntaxFactory.createVertexView(vertex.getKey(), vertex.getValue(), this));
	    vertex.getValue().addFocusListener(() -> {
		var halfWidth = getWidth() * 0.5;
		var halfHeight = getHeight() * 0.5;
		this.drawGroupTransform.setTx(halfWidth - vertex.getValue().position().getX());
		this.drawGroupTransform.setTy(halfHeight - vertex.getValue().position().getY());
		this.focus();
	    });
	}
	return nodes;
    }

    private Map<UUID,Node> initializeEdges() {
	var nodes = new HashMap<UUID,Node>();
	for(var edge : resource.syntax().edges().entrySet()) {
	    edge.getValue().addFocusListener(() -> {
		var sourcePos = resource.syntax().vertices().get(edge.getValue().source().get()).position();
		var targetPos = resource.syntax().vertices().get(edge.getValue().target().get()).position();
		var direction = targetPos.subtract(sourcePos).scale(0.5f);
		var center = sourcePos.add(direction);
		var halfWidth = getWidth() * 0.5;
		var halfHeight = getHeight() * 0.5;
		this.drawGroupTransform.setTx(halfWidth - center.getX());
		this.drawGroupTransform.setTy(halfHeight - center.getY());
		this.focus();
	    });
	    nodes.put(edge.getKey(), syntaxFactory.createEdgeView(edge.getKey(), edge.getValue(), this));
	}
	return nodes;
    }

    private void initializeMetadataEventHandlers() {
	resource.metadata().addListener((MapChangeListener<String,String>)e -> {
	    var changedKey = e.getKey();
	    switch(changedKey) {
		case "graphedit_syntax":
		    syntaxFactory = MetadataUtils.getSyntaxFactory(resource.metadata(), syntaxFactory);
		    initializeToolbar();
		    break;
		default: break;
	    }
	});
    }

    private void initializeToolEventHandlers() {
	Platform.runLater(() -> {
	    viewport.addEventHandler(MouseEvent.ANY, this::onMouseEvent);
	    getScene().addEventHandler(KeyEvent.ANY, this::onKeyEvent);
	});
    }

    private void onMouseEvent(MouseEvent e) {
	var toolbox = DI.get(IToolbox.class);
	// TODO: detect if an event has been "handled" - and call the syntax event first for maximal extendability
	var mouseEvent = new ViewportMouseEvent(e, drawGroupTransform, e.getTarget() == drawPane, syntaxFactory, resource.syntax(), settings);
	toolbox.getSelectedTool().get().onViewportMouseEvent(mouseEvent);
	var syntaxToolbox = syntaxFactory.getSyntaxTools();
	if(syntaxToolbox.isPresent())
	    syntaxToolbox.get().getSelectedTool().get().onViewportMouseEvent(mouseEvent);
    }

    private void onKeyEvent(KeyEvent e) {
	var toolbox = DI.get(IToolbox.class);
	// TODO: the "isTargetDrawpane" field solution is hacky and doesnt work in detached tabs. It should be fixed
	var keyEvent = new ViewportKeyEvent(e, drawGroupTransform, e.getTarget() == getParent().getParent(), syntaxFactory, resource.syntax(), settings);
	toolbox.getSelectedTool().get().onKeyEvent(keyEvent);
	var syntaxToolbox = syntaxFactory.getSyntaxTools();
	if(syntaxToolbox.isPresent())
	    syntaxToolbox.get().getSelectedTool().get().onKeyEvent(keyEvent);
    }

    private void initializeVertexCollectionChangeHandlers() {
	resource.syntax().vertices().addListener((MapChangeListener<UUID,ViewModelVertex>)c -> {
	    if(c.wasAdded()) {
		drawGroup.addChild(c.getKey(), syntaxFactory.createVertexView(c.getKey(), c.getValueAdded(), this));
		c.getValueAdded().addFocusListener(() -> {
		    var halfWidth = getWidth() * 0.5;
		    var halfHeight = getHeight() * 0.5;
		    this.drawGroupTransform.setTx(halfWidth - c.getValueAdded().position().getX());
		    this.drawGroupTransform.setTy(halfHeight - c.getValueAdded().position().getY());
		    this.focus();
		});
	    }
	    if(c.wasRemoved())
		drawGroup.removeChild(c.getKey());
	});
    }

    private void initializeEdgeCollectionChangeHandlers() {
	resource.syntax().edges().addListener((MapChangeListener<UUID,ViewModelEdge>)c -> {
	    if(c.wasAdded()) {
		c.getValueAdded().addFocusListener(() -> {
		    var sourcePos = resource.syntax().vertices().get(c.getValueAdded().source().get()).position();
		    var targetPos = resource.syntax().vertices().get(c.getValueAdded().target().get()).position();
		    var direction = targetPos.subtract(sourcePos).scale(0.5f);
		    var center = sourcePos.add(direction);
		    var halfWidth = getWidth() * 0.5;
		    var halfHeight = getHeight() * 0.5;
		    this.drawGroupTransform.setTx(halfWidth - center.getX());
		    this.drawGroupTransform.setTy(halfHeight - center.getY());
		    this.focus();
		});
		drawGroup.addChild(c.getKey(), syntaxFactory.createEdgeView(c.getKey(), c.getValueAdded(), this));
		drawGroup.getChild(c.getKey()).toBack();
	    }
	    if(c.wasRemoved())
		drawGroup.removeChild(c.getKey());
	});
    }

    public ViewModelProjectResource getProjectResource() {
	return resource;
    }

    public Affine getViewportTransform() {
	return drawGroupTransform;
    }

    public ViewModelEditorSettings getEditorSettings() {
	return settings;
    }

    @Override
    public void addFocusListener(Runnable focusEventHandler) {
	onFocusEventHandlers.add(focusEventHandler);
    }

    @Override
    public void focus() {
	onFocusEventHandlers.forEach(Runnable::run);
    }
}

