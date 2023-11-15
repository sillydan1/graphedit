package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.viewmodel.LintContainer;
import dk.gtz.graphedit.viewmodel.ViewModelLint;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;

public class LintPaneController extends Pane {
    private Logger logger = LoggerFactory.getLogger(LintPaneController.class);
    private final Affine transform;
    private final ViewModelProjectResource resource;

    public LintPaneController(String bufferKey, ViewModelProjectResource resource, Affine viewportAffine) {
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
	    var polygon = new SimpleObjectProperty<>(createConvexHull(vertices, lint));
	    resource.syntax().vertices().forEach((k, v) -> {
		v.addListener((e,o,n) -> {
		    getChildren().remove(polygon.get());
		    polygon.set(createConvexHull(vertices, lint));
		    getChildren().add(polygon.get());
		});
	    });
	    getChildren().add(polygon.get());
	}
    }

    private Polygon createConvexHull(Collection<ViewModelVertex> vertices, ViewModelLint lint) {
	var points = vertices.stream().flatMap(v -> {
	    var buffer = 5;
	    var sizeX = (v.shape().widthProperty().getValue() + buffer);
	    if(v.shape().shapeType().getValue().equals(ViewModelShapeType.RECTANGLE))
		sizeX = ((v.shape().widthProperty().getValue() / 2) + buffer);
	    var sizeY = (v.shape().heightProperty().getValue() + buffer);
	    if(v.shape().shapeType().getValue().equals(ViewModelShapeType.RECTANGLE))
		sizeY = ((v.shape().heightProperty().getValue() / 2) + buffer);
	    return List.of(
		    new ViewModelPoint(v.position().getXProperty().add(sizeX), v.position().getYProperty().add(sizeY)),
		    new ViewModelPoint(v.position().getXProperty().subtract(sizeX), v.position().getYProperty().subtract(sizeY)),
		    new ViewModelPoint(v.position().getXProperty().add(sizeX), v.position().getYProperty().subtract(sizeY)),
		    new ViewModelPoint(v.position().getXProperty().subtract(sizeX), v.position().getYProperty().add(sizeY))
		    ).stream();
	}).toList();
	var convexHull = quickHull(points);
	var polygon = new Polygon();
	for(var hullVertex : convexHull) {
	    polygon.getPoints().add(hullVertex.getX());
	    polygon.getPoints().add(hullVertex.getY());
	}
	polygon.strokeTypeProperty().set(StrokeType.INSIDE);
	polygon.setFill(Color.TRANSPARENT);
	switch(lint.severity().get()) {
	    case ERROR: polygon.getStyleClass().add("stroke-error"); break;
	    case WARNING: polygon.getStyleClass().add("stroke-warning"); break;
	    case INFO: polygon.getStyleClass().add("stroke-info"); break;
	    default: break;
	}
	return polygon;
    }

    private double getLineDistance(ViewModelPoint p1, ViewModelPoint p2, ViewModelPoint p) {
	return (p.getY()-p1.getY())*(p2.getX()-p1.getX())-(p2.getY()-p1.getY())*(p.getX()-p1.getX());
    }

    private List<ViewModelPoint> quickHull(List<ViewModelPoint> points) {
	if(points.size() <= 1)
	    return points;
	var hull = new ArrayList<ViewModelPoint>();
	ViewModelPoint a = null;
	ViewModelPoint b = null;
	var maxX = Double.MIN_VALUE;
	var minX = Double.MAX_VALUE;
	for(var point : points) {
	    if(point.getX() > maxX) {
		maxX = point.getX();
		b = point;
	    }
	    else if(point.getX() < minX) {
		minX = point.getX();
		a = point;
	    }
	}
	hull.add(a);
	hull.add(b);
	var s1 = new ArrayList<ViewModelPoint>();
	var s2 = new ArrayList<ViewModelPoint>();
	for(var point : points) {
	    if(point == a)
		continue;
	    if(point == b)
		continue;
	    if(getLineDistance(a, b, point) < 0)
		s1.add(point);
	    else
		s2.add(point);
	}
	findHull(s1, a, b, hull);
	findHull(s2, b, a, hull);
	return hull;
    }

    private void findHull(List<ViewModelPoint> sk, ViewModelPoint p, ViewModelPoint q, List<ViewModelPoint> hull) {
	if(sk.isEmpty())
	    return;
	var maxHeight = Double.MIN_VALUE;
	ViewModelPoint c = null;
	for(var point : sk) {
	    var height = -getLineDistance(p, q, point); // NOTE: Inverted, because y-axis is inverted in javafx
	    if(height < maxHeight)
		continue;
	    maxHeight = height;
	    c = point;
	}
	hull.add(hull.indexOf(p) + 1, c);
	var s1 = new ArrayList<ViewModelPoint>();
	var s2 = new ArrayList<ViewModelPoint>();
	for(var point : sk) {
	    if(point == c)
		continue;
	    if(getLineDistance(p, c, point) < 0)
		s1.add(point);
	    if(getLineDistance(c, q, point) < 0)
		s2.add(point);
	}
	findHull(s1, p, c, hull);
	findHull(s2, c, q, hull);
    }
}
