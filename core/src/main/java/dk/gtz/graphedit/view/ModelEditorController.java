package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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
	var r = new Random().nextDouble(6.0);
	var scale = new Scale(r,r);
	var group = new Group();
	group.getChildren().addAll(initializeLocations());
	group.getTransforms().add(scale);
	getChildren().add(group);
    }

    private List<Node> initializeLocations() {
	// Instantiate circles for each syntax().vertices().vertex
	var nodes = new ArrayList<Node>();
	for(var vertex : resource.syntax().vertices().entrySet()) {
	    var point = vertex.getValue().position();
	    var circ = new Circle();
	    circ.setCenterX(point.get().x);
	    circ.setCenterY(point.get().y);
	    nodes.add(circ);
	}
	return nodes;
    }
}

