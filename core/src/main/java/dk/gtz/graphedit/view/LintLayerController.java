package dk.gtz.graphedit.view;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.viewmodel.LintContainer;
import dk.gtz.graphedit.viewmodel.ViewModelLint;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

public class LintLayerController extends Pane {
    private Logger logger = LoggerFactory.getLogger(LintLayerController.class);
    private final Affine transform;
    private final ViewModelProjectResource resource;

    public LintLayerController(String bufferKey, ViewModelProjectResource resource, Affine viewportAffine) {
	this.transform = viewportAffine;
	this.resource = resource;
	getTransforms().add(transform);
	var lints = DI.get(LintContainer.class).get(bufferKey);
	setLints(lints);
	lints.addListener((e,o,n) -> setLints(n));
    }

    private void setLints(List<ViewModelLint> lints) {
	getChildren().clear();
	for(var lint : lints) {
	    var vertices = resource.syntax().vertices().entrySet().stream()
		.filter(s -> lint.affectedElements().contains(s.getKey()))
		.map(Entry::getValue).toList();
	    if(vertices.isEmpty())
		continue;
	    var polygon = createConvexHullPolygon(vertices, lint);
	    setFocusHandler(lint, polygon);
	    getChildren().add(polygon);
	}
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
