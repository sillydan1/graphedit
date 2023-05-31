package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Scale;

public class ModelEditorController extends VBox {
    private final ViewModelProjectResource resource;

    public ModelEditorController(ViewModelProjectResource resource) {
	this.resource = resource;
	initialize();
    }

    private void initialize() {
	// Subscribe to changes in syntax().vertices map
	// Subscribe to changes in syntax().edges map

	// <Circle radius="20" mouseTransparent="true" opacity="0"/>
	var group = new Group();
	group.getChildren().addAll(initializeLocations());
	getChildren().add(group);
    }

    private List<Node> initializeLocations() {
	// Instantiate circles for each syntax().vertices().vertex
        // <Circle fx:id="circle" strokeType="INSIDE" opacity="0"/>
        // <Circle fx:id="circleShakeIndicator" radius="10" mouseTransparent="true" fill="white" opacity="0"/>
	var nodes = new ArrayList<Node>();
	for(var vertex : resource.syntax().vertices().entrySet()) {
	    var point = vertex.getValue().position();
	    var circ = new Circle(20.0);
	    circ.strokeTypeProperty().set(StrokeType.INSIDE);
	    circ.fillProperty().set(Paint.valueOf("#fff"));
	    circ.setCenterX(point.get().x);
	    circ.setCenterY(point.get().y);
	    nodes.add(circ);
	}
	return nodes;
    }
}

