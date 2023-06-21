package dk.gtz.graphedit.view;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.TransformChangedEvent;

public class GridPane extends Pane {
    private final double gridsizeX;
    private final double gridsizeY;
    private double offsetX;
    private double offsetY;
    private double gridscaleX;
    private double gridscaleY;

    public GridPane(double gridsize, Affine transform) {
	this(gridsize, gridsize, transform);
    }

    public GridPane(double gridsizeX, double gridsizeY, Affine transform) {
	super();
	this.gridsizeX = gridsizeX;
	this.gridsizeY = gridsizeY;
	this.offsetX = 0.0;
	this.offsetY = 0.0;
	this.gridscaleX = 1.0;
	this.gridscaleY = 1.0;
	initializeTransformEventHandler(transform);
    }

    @Override
    public void layoutChildren() {
	// TODO: this is quite expensive, please reuse the children
	getChildren().clear();
	var width = getWidth();
	var height = getHeight();
	var adjustedGridsizeX = gridsizeX * gridscaleX;
	var adjustedGridsizeY = gridsizeY * gridscaleY;

	// column lines
	for (var x = -adjustedGridsizeX; x < width + adjustedGridsizeX; x += adjustedGridsizeX) {
	    var xx = x + offsetX;
	    var line = new Line(xx, 0, xx, height);
	    line.styleProperty().set("-fx-stroke: -color-bg-subtle");
	    getChildren().add(line);
	}

	// row lines
	for (var y = -adjustedGridsizeY; y < height + adjustedGridsizeY; y += adjustedGridsizeY) {
	    var yy = y + offsetY;
	    var line = new Line(0, yy, width, yy);
	    line.styleProperty().set("-fx-stroke: -color-bg-subtle");
	    getChildren().add(line);
	}
    }

    private void initializeTransformEventHandler(Affine newTransform) {
	newTransform.addEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, (e) -> {
	    gridscaleX = newTransform.getMxx();
	    gridscaleY = newTransform.getMyy();
	    offsetX = newTransform.getTx() % (gridsizeX * gridscaleX);
	    offsetY = newTransform.getTy() % (gridsizeY * gridscaleY);
	    layoutChildren();
	});
    }
}

