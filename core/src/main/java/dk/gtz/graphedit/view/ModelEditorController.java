package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.util.DragUtil;
import dk.gtz.graphedit.util.MapGroup;
import dk.gtz.graphedit.util.MetadataUtils;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
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

/**
 * View controller for the main model editor viewport.
 */
public class ModelEditorController extends BorderPane implements IFocusable {
    private static float ZOOM_SPEED_SCALAR = 0.01f;
    private final ViewModelProjectResource resource;
    private final ViewModelEditorSettings settings;
    private final String bufferKey;
    private ISyntaxFactory syntaxFactory;
    private StackPane viewport;
    private ModelEditorToolbar toolbar;
    private MapGroup<UUID> drawGroup;
    private GridPaneController gridPane;
    private LintLayerController lintPane;
    private Pane drawPane;
    private Affine drawGroupTransform;
    private List<Runnable> onFocusEventHandlers;

    /**
     * Creates a new instance with a provided resource and syntax
     * @param bufferKey The key of the related buffer
     * @param resource The viewmodel project resource to edit
     * @param syntaxFactory The syntax factory of the resource
     */
    public ModelEditorController(String bufferKey, ViewModelProjectResource resource, ISyntaxFactory syntaxFactory) {
	this(bufferKey, resource, DI.get(ViewModelEditorSettings.class), syntaxFactory);
    }

    /**
     * Creates a new instance with a provided resource, settings object and syntax
     * @param bufferKey The key of the related buffer
     * @param resource The viewmodel project resource to edit
     * @param settings The viewmodel editor settings object to use
     * @param syntaxFactory The syntax factory of the resource
     */
    public ModelEditorController(String bufferKey, ViewModelProjectResource resource, ViewModelEditorSettings settings, ISyntaxFactory syntaxFactory) {
	this.bufferKey = bufferKey;
	this.resource = resource;
	this.settings = settings;
	this.syntaxFactory = syntaxFactory;
	this.onFocusEventHandlers = new ArrayList<>();
	initialize();
    }

    private void initialize() {
	initializeViewport();
	initializeToolbar();
	initializeViewportLayers();
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

    private void initializeViewportLayers() {
	drawGroup = new MapGroup<>();
	drawGroupTransform = new Affine();
	drawGroup.addChildren(initializeEdges());
	drawGroup.addChildren(initializeVertices());
	drawGroup.addTransform(drawGroupTransform);

	drawPane = new Pane(drawGroup.getGroup());
	drawPane.setOnScroll(this::onScrollingDrawPane);
	drawPane.setOnZoom(this::onZoomDrawPane);
	DragUtil.makeDraggableInverse(drawPane, drawGroupTransform);
	drawPane.prefWidthProperty().bind(widthProperty());
	drawPane.prefHeightProperty().bind(heightProperty());

	gridPane = new GridPaneController(settings.gridSizeX(), settings.gridSizeY(), drawGroupTransform);
	settings.gridSizeX().addListener((e,o,n) -> gridPane.setGridSize(n.doubleValue(), settings.gridSizeY().get()));
	settings.gridSizeY().addListener((e,o,n) -> gridPane.setGridSize(settings.gridSizeY().get(), n.doubleValue()));

	lintPane = new LintLayerController(bufferKey, resource, drawGroupTransform);

	viewport.getChildren().add(gridPane);
	viewport.getChildren().add(lintPane);
	viewport.getChildren().add(drawPane);
    }

    private void onScrollingDrawPane(ScrollEvent event) {
	if(event.isControlDown())
	    zoomDrawPane(1 - (event.getDeltaY() * ZOOM_SPEED_SCALAR));
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
	    nodes.put(vertex.getKey(), syntaxFactory.createVertexView(bufferKey, vertex.getKey(), vertex.getValue(), resource.syntax(), getViewportTransform()));
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
	    nodes.put(edge.getKey(), syntaxFactory.createEdgeView(bufferKey, edge.getKey(), edge.getValue(), resource.syntax(), getViewportTransform()));
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
		default:
		    break;
	    }
	});
    }

    private void initializeToolEventHandlers() {
	viewport.addEventHandler(MouseEvent.ANY, this::onMouseEvent);
    }

    /**
     * Propogate the provided mouse event to the appropriate tool.
     * @param e The mouse event to handle.
     */
    public void onMouseEvent(MouseEvent e) {
	// TODO: detect if an event has been "handled" - and call the syntax event first for maximal extendability
	var mouseEvent = new ViewportMouseEvent(e, drawGroupTransform, e.getTarget() == drawPane, syntaxFactory, resource.syntax(), bufferKey, settings);
	DI.get(IToolbox.class).getSelectedTool().get().onViewportMouseEvent(mouseEvent);
	var syntaxToolbox = syntaxFactory.getSyntaxTools();
	if(syntaxToolbox.isPresent())
	    syntaxToolbox.get().getSelectedTool().get().onViewportMouseEvent(mouseEvent);
    }

    /**
     * Propogate the provided key event to the appropriate tool.
     * @param e The key event to handle.
     */
    public void onKeyEvent(KeyEvent e) {
	// TODO: the "isTargetDrawpane" field solution is hacky and doesnt work in detached tabs. It should be fixed
	var isTargetDrawpane = e.getTarget() == getParent().getParent();
	var keyEvent = new ViewportKeyEvent(e, drawGroupTransform, isTargetDrawpane, syntaxFactory, resource.syntax(), bufferKey, settings);
	DI.get(IToolbox.class).getSelectedTool().get().onKeyEvent(keyEvent);
	var syntaxToolbox = syntaxFactory.getSyntaxTools();
	if(syntaxToolbox.isPresent())
	    syntaxToolbox.get().getSelectedTool().get().onKeyEvent(keyEvent);
    }

    private void initializeVertexCollectionChangeHandlers() {
	resource.syntax().vertices().addListener((MapChangeListener<UUID,ViewModelVertex>)c -> {
	    if(c.wasAdded()) {
		drawGroup.addChild(c.getKey(), syntaxFactory.createVertexView(bufferKey, c.getKey(), c.getValueAdded(), resource.syntax(), getViewportTransform()));
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
		drawGroup.addChild(c.getKey(), syntaxFactory.createEdgeView(bufferKey, c.getKey(), c.getValueAdded(), resource.syntax(), getViewportTransform()));
		drawGroup.getChild(c.getKey()).toBack();
	    }
	    if(c.wasRemoved())
		drawGroup.removeChild(c.getKey());
	});
    }

    /**
     * Get the resource being edited
     * @return The viewmodel project resource that is being edited by this editor view controller
     */
    public ViewModelProjectResource getProjectResource() {
	return resource;
    }

    /**
     * Get the transform, scale and rotation matrix of the viewport
     * @return The affine that governs the view of the viewport
     */
    public Affine getViewportTransform() {
	return drawGroupTransform;
    }

    /**
     * Get the current editor settings
     * @return The current viewmodel editor settings object
     */
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
