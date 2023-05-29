package dk.gtz.graphedit.view;

import java.util.Random;

import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.scene.Group;
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
	// Instantiate circles for each syntax().vertices().vertex

	// Subscribe to changes in syntax().vertices map
	// Subscribe to changes in syntax().edges map

	// <Circle radius="20" mouseTransparent="true" opacity="0"/>
	var c = new Circle(20);
	var r = new Random().nextDouble(6.0);
	c.mouseTransparentProperty().set(true);
	var scale = new Scale(r,r);
	var group = new Group();
	group.getChildren().add(c);
	group.getTransforms().add(scale);
	getChildren().add(group);
    }
}

