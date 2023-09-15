package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.TransformChangedEvent;

/**
 * A javafx {@link Pane} with a movable line-grid background.
 * Not to be confused with {@link javafx.scene.layout.GridPane}
 */
public class GridPane extends Pane {
    private DoubleProperty gridsizeX;
    private DoubleProperty gridsizeY;
    private double offsetX;
    private double offsetY;
    private double gridscaleX;
    private double gridscaleY;
    private List<Line> linePool;

    /**
     * Constructs a new GridPane with a square grid of a specified size and transformation matrix.
     * @param gridsize the width and height size of the grid
     * @param transform the transform matrix. Use this to zoom in/out and translate the grid
     */
    public GridPane(DoubleProperty gridsize, Affine transform) {
	this(gridsize, gridsize, transform);
    }

    /**
     * Constructs a new GridPane with a rectangle grid of a specified size and transformation matrix.
     * @param gridsizeX the width size of the grid
     * @param gridsizeY the height size of the grid
     * @param transform the transform matrix. Use this to zoom in/out and translate the grid
     */
    public GridPane(DoubleProperty gridsizeX, DoubleProperty gridsizeY, Affine transform) {
	super();
	this.gridsizeX = gridsizeX;
	this.gridsizeY = gridsizeY;
	this.offsetX = 0.0;
	this.offsetY = 0.0;
	this.gridscaleX = 1.0;
	this.gridscaleY = 1.0;
	this.linePool = new ArrayList<>();
	initializeTransformEventHandler(transform);
    }

    @Override
    public void layoutChildren() {
	refreshLinesPooled();
    }

    private void refreshLinesPooled() {
	var width = getWidth();
	var height = getHeight();
	var adjustedGridsizeX = gridsizeX.get() * gridscaleX;
	var adjustedGridsizeY = gridsizeY.get() * gridscaleY;
	var i = 0;

	// column lines
	for (var x = -adjustedGridsizeX; x < width + adjustedGridsizeX; x += adjustedGridsizeX) {
	    var xx = x + offsetX;
	    var line = getFromPool(i++);
	    line.setStartX(xx);
	    line.setStartY(0);
	    line.setEndX(xx);
	    line.setEndY(height);
	    line.setVisible(true);
	}

	// row lines
	for (var y = -adjustedGridsizeY; y < height + adjustedGridsizeY; y += adjustedGridsizeY) {
	    var yy = y + offsetY;
	    var line = getFromPool(i++);
	    line.setStartX(0);
	    line.setStartY(yy);
	    line.setEndX(width);
	    line.setEndY(yy);
	    line.setVisible(true);
	}

	for( ; i < linePool.size(); i++)
	    getFromPool(i).setVisible(false);
    }

    private Line getFromPool(int index) {
	if(index > linePool.size()-1) {
	    var newLine = new Line();
	    newLine.styleProperty().set("-fx-stroke: -color-bg-subtle");
	    linePool.add(newLine);
	    getChildren().add(newLine);
	    return newLine;
	}
	if(index < 0)
	    throw new IndexOutOfBoundsException();
	return linePool.get(index);
    }

    @Deprecated
    private void refreshLines() {
	getChildren().clear();
	var width = getWidth();
	var height = getHeight();
	var adjustedGridsizeX = gridsizeX.get() * gridscaleX;
	var adjustedGridsizeY = gridsizeY.get() * gridscaleY;

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

    /**
     * Change the grid size to some new value
     * @param gridsizeX the new width of the grid
     * @param gridsizeY the new height of the grid
     */
    public void setGridSize(double gridsizeX, double gridsizeY) {
	this.gridsizeX.set(gridsizeX);
	this.gridsizeY.set(gridsizeY);
	Platform.runLater(this::layoutChildren);
    }

    private void initializeTransformEventHandler(Affine newTransform) {
	newTransform.addEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, (e) -> {
	    gridscaleX = newTransform.getMxx();
	    gridscaleY = newTransform.getMyy();
	    offsetX = newTransform.getTx() % (gridsizeX.get() * gridscaleX);
	    offsetY = newTransform.getTy() % (gridsizeY.get() * gridscaleY);
	    layoutChildren();
	});
	gridsizeX.addListener((e) -> layoutChildren());
	gridsizeY.addListener((e) -> layoutChildren());
    }
}

