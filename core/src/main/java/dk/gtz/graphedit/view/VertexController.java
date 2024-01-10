package dk.gtz.graphedit.view;

import java.util.UUID;

import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

/**
 * The view baseclass for graph vertices.
 * Contains all the logic needed for the demonstration syntax.
 * If you extend from this and dont want all the features, you should overwrite the unwanted initialize functions
 */
public class VertexController extends StackPane {
    /**
     * The id of the represented vertex
     */
    protected final UUID vertexKey;

    /**
     * The viewmodel vertex that this view controller represents
     */
    protected final ViewModelVertex vertexValue;

    /**
     * Reference to the transform, scale and rotation matrix of the containing viewport
     */
    protected final Affine viewportAffine;

    /**
     * The graphic representation of the vertex. This may be subject to change
     */
    protected Circle graphic;

    /**
     * The associated syntax factory
     */
    protected ISyntaxFactory syntaxFactory;

    /**
     * Construct a new instance
     * @param vertexKey The id of the vertex to represent
     * @param vertex The viewmodel vertex to represent
     * @param viewportAffine The transform, scale and rotation matrix of the containing viewport
     * @param graph The parent graph containing the represented viewmodel vertex
     * @param editorSettings The current editor settings
     * @param selectedTool The object property specifying which tool is currently selected
     * @param syntaxFactory The associated syntax factory
     * @param bufferKey The key of the buffer that contains the vertex
     */
    public VertexController(UUID vertexKey, ViewModelVertex vertex, Affine viewportAffine, ViewModelGraph graph, ViewModelEditorSettings editorSettings, ObjectProperty<ITool> selectedTool, ISyntaxFactory syntaxFactory, String bufferKey) {
	this.vertexKey = vertexKey;
	this.vertexValue = vertex;
	this.viewportAffine = viewportAffine;
	this.syntaxFactory = syntaxFactory;
	this.graphic = getVertexCircle();
	initialize();
	initializeEventHandlers(selectedTool, graph, editorSettings, bufferKey);
    }

    /**
     * Initializer function, will be called during the constructor call
     */
    protected void initialize() {
	addTooltipHoverListener();
	addGraphic();
	addLabel();
	addFocusPulseEvent();
	addCursorHoverEffect();
	bindTranslatePropertiesToViewmodel();
	bindSizePropertiesToViewmodel();
    }

    /**
     * Initializer function that adds a tooltip hover effect when it is available.
     */
    protected void addTooltipHoverListener() {
	var t = new Tooltip();
	this.vertexValue.addHoverListener((e,o,n) -> {
	    if(n == null)
		Tooltip.uninstall(this, t);
	    else {
		t.setGraphic(n);
		Tooltip.install(this, t);
	    }
	});
    }

    /**
     * Initializer function that adds the graphic to the display stack. Called during {@link #initialize}
     */
    protected void addGraphic() {
	getChildren().add(graphic);
    }

    /**
     * Initializer function that adds a label to the display stack. Called during {@link #initialize}
     */
    protected void addLabel() {
	var label = new Label(vertexKey.toString());
	label.getStyleClass().add("outline");
	getChildren().add(label);
    }

    /**
     * Initializer function that adds a pulse animation to the focus event. Called during {@link #initialize}
     */
    protected void addFocusPulseEvent() {
	var pulseTimeline = createPulseTimeline(1.1, Duration.millis(300));
	this.vertexValue.addFocusListener(() -> {
	    pulseTimeline.playFromStart();
	    this.requestFocus();
	});
    }

    private Timeline createPulseTimeline(double intensity, Duration timelineTime) {
        var scale1x = new KeyValue(vertexValue.shape().scaleXProperty(), 1, Interpolator.EASE_BOTH);
        var scale2x = new KeyValue(vertexValue.shape().scaleXProperty(), 1 * intensity, Interpolator.EASE_BOTH);
        var scale3x = new KeyValue(vertexValue.shape().scaleXProperty(), 1, Interpolator.EASE_BOTH);
        var scale1y = new KeyValue(vertexValue.shape().scaleYProperty(), 1, Interpolator.EASE_BOTH);
        var scale2y = new KeyValue(vertexValue.shape().scaleYProperty(), 1 * intensity, Interpolator.EASE_BOTH);
        var scale3y = new KeyValue(vertexValue.shape().scaleYProperty(), 1, Interpolator.EASE_BOTH);
        var kx1 = new KeyFrame(Duration.millis(0), scale1x);
        var kx2 = new KeyFrame(timelineTime.multiply(0.5), scale2x);
        var kx3 = new KeyFrame(timelineTime, scale3x);
        var ky1 = new KeyFrame(Duration.millis(0), scale1y);
        var ky2 = new KeyFrame(timelineTime.multiply(0.5), scale2y);
        var ky3 = new KeyFrame(timelineTime, scale3y);
	return new Timeline(kx1, ky1, kx2, ky2, kx3, ky3);
    }

    /**
     * Initializer function that adds a scale animation and a stroke indication on mouse hover. Called during {@link #initialize}
     */
    protected void addCursorHoverEffect() {
	setCursor(Cursor.HAND);
	var hoverEnterAnimation = createScaleTimeline(1, 1.1, Duration.millis(100));
	var hoverExitAnimation = createScaleTimeline(1.1, 1, Duration.millis(100));
	addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
	    hoverEnterAnimation.play();
	    graphic.getStyleClass().add("stroke-hover");
	});
	addEventHandler(MouseEvent.MOUSE_EXITED,  event -> {
	    hoverExitAnimation.play();
	    graphic.getStyleClass().remove("stroke-hover");
	});
    }

    private Timeline createScaleTimeline(double scaleBegin, double scaleEnd, Duration timelineTime) {
        var scale1x = new KeyValue(vertexValue.shape().scaleXProperty(), scaleBegin, Interpolator.EASE_BOTH);
        var scale1y = new KeyValue(vertexValue.shape().scaleYProperty(), scaleBegin, Interpolator.EASE_BOTH);
        var scale2x = new KeyValue(vertexValue.shape().scaleXProperty(), scaleEnd, Interpolator.EASE_BOTH);
        var scale2y = new KeyValue(vertexValue.shape().scaleYProperty(), scaleEnd, Interpolator.EASE_BOTH);
        var kx1 = new KeyFrame(Duration.millis(0), scale1x);
        var ky1 = new KeyFrame(Duration.millis(0), scale1y);
        var kx2 = new KeyFrame(timelineTime, scale2x);
        var ky2 = new KeyFrame(timelineTime, scale2y);
	return new Timeline(kx1, ky1, kx2, ky2);
    }

    private Circle getVertexCircle() {
	var diameter = 20.0;
	var circle = new Circle(diameter);
	if(!vertexValue.shape().widthProperty().isBound())
	    vertexValue.shape().widthProperty().set(diameter);
	if(!vertexValue.shape().heightProperty().isBound())
	    vertexValue.shape().heightProperty().set(diameter);
	circle.strokeTypeProperty().set(StrokeType.INSIDE);
	circle.getStyleClass().add("vertex-node");
	return circle;
    }

    /**
     * Binds this' translate x and y properties to the associated vertex viewmodel position point.
     */
    protected void bindTranslatePropertiesToViewmodel() {
	var vertexXProperty = vertexValue.position().getXProperty();
	var vertexYProperty = vertexValue.position().getYProperty();
	translateXProperty().bind(vertexXProperty.subtract(widthProperty().divide(2)));
	translateYProperty().bind(vertexYProperty.subtract(heightProperty().divide(2)));
    }

    /**
     * Binds this' scale x and y properties to the associated vertex viewmodel shape's scale.
     */
    protected void bindSizePropertiesToViewmodel() {
	vertexValue.shape().scaleXProperty().set(scaleXProperty().get());
	vertexValue.shape().scaleYProperty().set(scaleYProperty().get());
	scaleXProperty().bind(vertexValue.shape().scaleXProperty());
	scaleYProperty().bind(vertexValue.shape().scaleYProperty());
    }

    /**
     * Set the standard vertex view controller event handlers
     * @param selectedTool Object property pointing to the currently selected tool
     * @param graph Graph that contains the representative vertex
     * @param editorSettings Current settings object
     * @param bufferKey Key of the associated buffer resource
     */
    protected void initializeEventHandlers(ObjectProperty<ITool> selectedTool, ViewModelGraph graph, ViewModelEditorSettings editorSettings, String bufferKey) {
	addEventHandler(MouseEvent.ANY, e -> {
	    var event = new VertexMouseEvent(e, vertexKey, vertexValue, viewportAffine, syntaxFactory, graph, bufferKey, editorSettings);
	    var syntaxToolbox = syntaxFactory.getSyntaxTools();
	    if(syntaxToolbox.isPresent())
		syntaxToolbox.get().getSelectedTool().get().onVertexMouseEvent(event);
	    selectedTool.get().onVertexMouseEvent(event);
	});
	vertexValue.getIsSelected().addListener((e,o,n) -> {
	    if(n)
		graphic.getStyleClass().add("stroke-selected");
	    else
		graphic.getStyleClass().remove("stroke-selected");
	});
    }
}
