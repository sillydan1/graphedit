package dk.gtz.graphedit.view.util;

import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.shape.Line;

public class BindingsUtil {
    public static DoubleBinding createOvalXBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	return Bindings.createDoubleBinding(() -> {
	    var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
	    var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
	    var cosDiff = Math.cos(Math.atan2(diffY, diffX));
	    return targetPosition.getXProperty().get() - (shape.widthProperty().get() * shape.scaleXProperty().get() * cosDiff);
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(), 
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.widthProperty(), shape.scaleXProperty());
    }

    public static DoubleBinding createOvalYBinding(ViewModelPoint sourcePosition, ViewModelPoint targetPosition, ViewModelVertexShape shape) {
	return Bindings.createDoubleBinding(() -> {
	    var diffX = targetPosition.getXProperty().get() - sourcePosition.getXProperty().get();
	    var diffY = targetPosition.getYProperty().get() - sourcePosition.getYProperty().get();
	    var sinDiffY = Math.sin(Math.atan2(diffY, diffX));
	    return targetPosition.getYProperty().get() - (shape.heightProperty().get() * shape.scaleYProperty().get() * sinDiffY);
	},
	sourcePosition.getXProperty(), sourcePosition.getYProperty(), 
	targetPosition.getXProperty(), targetPosition.getYProperty(),
	shape.heightProperty(), shape.scaleYProperty());
    }

    public static DoubleBinding createRotationAtLineEndBinding(Line line) {
	return Bindings.createDoubleBinding(() -> 
		    (Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX()) * 180 / Math.PI),
		    line.endYProperty(), line.endXProperty(), line.startXProperty(), line.startYProperty());
    }
}

