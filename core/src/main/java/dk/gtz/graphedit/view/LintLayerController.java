package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import dk.gtz.graphedit.util.BindingsUtil;
import dk.gtz.graphedit.util.MouseTracker;
import dk.gtz.graphedit.viewmodel.LintContainer;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelLint;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import dk.yalibs.yadi.DI;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

/**
 * View layer showing the lints contained in the {@link LintContainer}.
 */
public class LintLayerController extends Pane {
    private final Affine transform;
    private final ViewModelProjectResource resource;
    protected final MouseTracker tracker;

    /**
     * Constructs a new lint layer view controller.
     * @param bufferKey The buffer to use.
     * @param resource The related resource.
     * @param viewportAffine The position, rotation and scale transform of the related viewport.
     */
    public LintLayerController(String bufferKey, ViewModelProjectResource resource, Affine viewportAffine) {
	this.transform = viewportAffine;
	this.resource = resource;
	this.tracker = DI.get(MouseTracker.class);
	getTransforms().add(transform);
	var lints = DI.get(LintContainer.class).get(bufferKey);
	setLints(lints);
	lints.addListener((e,o,n) -> setLints(n));
    }

    private void setLints(List<ViewModelLint> lints) {
	var newNodes = new ArrayList<Node>();
	lints.forEach(lint -> {
	    var vertices = resource.syntax().vertices().entrySet().stream()
		.filter(s -> lint.affectedElements().contains(s.getKey()))
		.map(Entry::getValue).toList();
	    var edges = resource.syntax().edges().entrySet().stream()
		.filter(s -> lint.affectedElements().contains(s.getKey()))
		.map(Entry::getValue).toList();
	    if(!vertices.isEmpty()) {
		var polygon = createConvexHullPolygon(vertices, lint);
		setFocusHandler(lint, polygon);
		newNodes.add(polygon);
	    }
	    if(!edges.isEmpty()) {
		for(var edge : edges)
		    newNodes.add(createLintLine(edge, lint));
	    }
	});
	Platform.runLater(() -> {
	    getChildren().clear();
	    getChildren().addAll(newNodes);
	});
    }

    private PointShape getPointShape(UUID lookupId) {
	if(lookupId.equals(tracker.getTrackerUUID()))
	    return new PointShape(new ViewModelPoint(
			BindingsUtil.createAffineOffsetXBinding(tracker.getXProperty(), transform),
			BindingsUtil.createAffineOffsetYBinding(tracker.getYProperty(), transform)),
		    new ViewModelVertexShape(1,1,10,10,ViewModelShapeType.OVAL));
	var sourceVertex = resource.syntax().vertices().getValue().get(lookupId);
	return new PointShape(sourceVertex.position(), sourceVertex.shape());
    }

    private Line createLintLine(ViewModelEdge edge, ViewModelLint lint) {
	var edgePresentation = new Line();
	var source = getPointShape(edge.source().get());
	var target = getPointShape(edge.target().get());
	edgePresentation.startXProperty().bind(BindingsUtil.createShapedXBinding(target.point(), source.point(), source.shape()));
	edgePresentation.startYProperty().bind(BindingsUtil.createShapedYBinding(target.point(), source.point(), source.shape()));
	edgePresentation.endXProperty().bind(BindingsUtil.createShapedXBinding(source.point(), target.point(), target.shape()));
	edgePresentation.endYProperty().bind(BindingsUtil.createShapedYBinding(source.point(), target.point(), target.shape()));

	edgePresentation.strokeWidthProperty().set(10);
	switch(lint.severity().get()) {
	    case ERROR: edgePresentation.getStyleClass().add("stroke-error"); break;
	    case WARNING: edgePresentation.getStyleClass().add("stroke-warning"); break;
	    case INFO: edgePresentation.getStyleClass().add("stroke-info"); break;
	    default: break;
	}
	return edgePresentation;
    }

    private Timeline createPulseTimeline(Node node, double intensity, Duration timelineTime) {
        var scale1x = new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH);
        var scale2x = new KeyValue(node.scaleXProperty(), 1 * intensity, Interpolator.EASE_BOTH);
        var scale3x = new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH);
        var scale1y = new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH);
        var scale2y = new KeyValue(node.scaleYProperty(), 1 * intensity, Interpolator.EASE_BOTH);
        var scale3y = new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH);
        var kx1 = new KeyFrame(Duration.millis(0), scale1x);
        var kx2 = new KeyFrame(timelineTime.multiply(0.5), scale2x);
        var kx3 = new KeyFrame(timelineTime, scale3x);
        var ky1 = new KeyFrame(Duration.millis(0), scale1y);
        var ky2 = new KeyFrame(timelineTime.multiply(0.5), scale2y);
        var ky3 = new KeyFrame(timelineTime, scale3y);
	return new Timeline(kx1, ky1, kx2, ky2, kx3, ky3);
    }

    private void setFocusHandler(ViewModelLint lintToFocusOn, ConvexHullPolygonController representingPolygon) {
	var pulseTimeline = createPulseTimeline(representingPolygon, 1.1, Duration.millis(300));
	lintToFocusOn.addFocusListener(() -> {
	    pulseTimeline.playFromStart();
	    var polyCenter = representingPolygon.getCenter();
	    transform.setTx(polyCenter.getX());
	    transform.setTy(polyCenter.getY());
	    resource.focus();
	});
    }

    private ConvexHullPolygonController createConvexHullPolygon(Collection<ViewModelVertex> vertices, ViewModelLint lint) {
	var points = vertices.stream().flatMap(v -> {
	    var buffer = 5;
	    var sizeX = v.shape().widthProperty().add(buffer);
	    if(v.shape().shapeType().getValue().equals(ViewModelShapeType.RECTANGLE))
		sizeX = v.shape().widthProperty().divide(2).add(buffer);
	    var sizeY = v.shape().heightProperty().add(buffer);
	    if(v.shape().shapeType().getValue().equals(ViewModelShapeType.RECTANGLE))
		sizeY = v.shape().heightProperty().divide(2).add(buffer);
	    return List.of(
		    new ViewModelPoint(v.position().getXProperty().add(sizeX), v.position().getYProperty().add(sizeY)),
		    new ViewModelPoint(v.position().getXProperty().subtract(sizeX), v.position().getYProperty().subtract(sizeY)),
		    new ViewModelPoint(v.position().getXProperty().add(sizeX), v.position().getYProperty().subtract(sizeY)),
		    new ViewModelPoint(v.position().getXProperty().subtract(sizeX), v.position().getYProperty().add(sizeY))
		    ).stream();
	}).toList();
	var polygon = new ConvexHullPolygonController(points);
	switch(lint.severity().get()) {
	    case ERROR: polygon.addStyleClass("stroke-error"); break;
	    case WARNING: polygon.addStyleClass("stroke-warning"); break;
	    case INFO: polygon.addStyleClass("stroke-info"); break;
	    default: break;
	}
	return polygon;
    }
}
