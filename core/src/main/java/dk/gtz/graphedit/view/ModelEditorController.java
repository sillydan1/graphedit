package dk.gtz.graphedit.view;

import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ModelEditorController extends VBox {
    private final ViewModelProjectResource resource;

    public ModelEditorController(ViewModelProjectResource resource) {
	this.resource = resource;
	initialize();
    }

    private void initialize() {
	var c = new Circle(20);
	c.mouseTransparentProperty().set(true);
	getChildren().add(c);
    }
}

