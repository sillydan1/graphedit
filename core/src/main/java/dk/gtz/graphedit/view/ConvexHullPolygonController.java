package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.List;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

/**
 * Responsive polygon that fits to the convex hull of a set points.
 * The convex hull is calculated using the quickhull algorithm.
 */
public class ConvexHullPolygonController extends Group {
    private final List<ViewModelPoint> points;
    private ObjectProperty<StrokeType> strokeTypeProperty;
    private Color fill;
    private Polygon polygon;

    /**
     * Constructs a new convex hull polygon view
     * @param points The points to calculate the convex hull from
     */
    public ConvexHullPolygonController(List<ViewModelPoint> points) {
	this.points = points;
	fill = Color.TRANSPARENT;
	strokeTypeProperty = new SimpleObjectProperty<>(StrokeType.INSIDE);
	this.polygon = getPolygon();
	initializeEvents();
	getChildren().add(polygon);
    }

    private void initializeEvents() {
	for(var point : points)
	    point.addListener(this::updateOnChangedEvent);
	parentProperty().addListener((e,o,n) -> {
	    if(n == null)
		for(var point : points)
		    point.removeListener(this::updateOnChangedEvent);
	});
    }

    /**
     * Set polygon fill color
     * @param fillColor The color to fill the convex hull polygon with
     */
    public void setFill(Color fillColor) {
	this.fill = fillColor;
    }

    /**
     * Get the center point of the polygon.
     * Note that this point doesn't automatically update.
     * @return The current center of the convex hull polygon
     */
    public ViewModelPoint getCenter() {
	var points = polygon.getPoints();
	var result = new ViewModelPoint(0,0);
	for(var i = 0; i < points.size(); i += 2) {
	    result.getXProperty().set(result.getX() + points.get(i));
	    result.getYProperty().set(result.getY() + points.get(i+1));
	}
	result.getXProperty().set(result.getX() / points.size());
	result.getYProperty().set(result.getY() / points.size());
	return result;
    }

    private void updateOnChangedEvent(ObservableValue<? extends ViewModelPoint> e, ViewModelPoint o, ViewModelPoint n) {
	update();
    }

    private void update() {
	getChildren().remove(polygon);
	polygon = getPolygon();
	getChildren().add(polygon);
    }

    private Polygon getPolygon() {
	var result = new Polygon();
	result.setFill(fill);
	result.strokeTypeProperty().set(strokeTypeProperty.get());
	var hullPoints = getHull();
	for(var point : hullPoints) {
	    result.getPoints().add(point.getX());
	    result.getPoints().add(point.getY());
	}
	result.getStyleClass().addAll(getStyleClass());
	return result;
    }

    /**
     * Add a styleclass to the polygon
     * @param styleClass The new CSS style class to add
     */
    public void addStyleClass(String styleClass) {
	getStyleClass().add(styleClass);
	polygon.getStyleClass().add(styleClass);
    }

    private List<ViewModelPoint> getHull() {
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
	if(c == null)
	    return;
	hull.add(hull.indexOf(p) + 1, c);
	var s1 = new ArrayList<ViewModelPoint>();
	var s2 = new ArrayList<ViewModelPoint>();
	for(var point : sk) {
	    if(point == c)
		continue;
	    if(-getLineDistance(p, c, point) > 0)
		s1.add(point);
	    if(-getLineDistance(c, q, point) > 0)
		s2.add(point);
	}
	findHull(s1, p, c, hull);
	findHull(s2, c, q, hull);
    }

    private double getLineDistance(ViewModelPoint p1, ViewModelPoint p2, ViewModelPoint p) {
	return (p.getY()-p1.getY())*(p2.getX()-p1.getX())-(p2.getY()-p1.getY())*(p.getX()-p1.getX());
    }
}
