package dk.gtz.graphedit.view;

import java.util.ArrayList;

import dk.gtz.graphedit.view.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.gtz.graphedit.viewmodel.IInspectable;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.yalibs.yadi.DI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InspectorController {
    @FXML
    private VBox propertiesContainer;
    private final ObservableList<ISelectable> selectedElements;

    public InspectorController() {
	selectedElements = DI.get("selectedElements");
    }

    @FXML
    private void initialize() {
	addAllSelected();
	initializeSelectionEventHandlers();
    }

    private void addAllSelected() {
	for(var element : selectedElements) {
	    if(element instanceof IInspectable inspectable)
		addInspectable(inspectable);
	}
    }

    private void initializeSelectionEventHandlers() {
	selectedElements.addListener((ListChangeListener<ISelectable>)(n) -> {
	    propertiesContainer.getChildren().clear();
	    addAllSelected();
	});
    }

    private void addInspectable(IInspectable inspectable) {
	var inspectableProperties = inspectable.getInspectableObjects();
	var inspectors = new ArrayList<Node>();
	inspectors.add(new Label(inspectable.getClass().getSimpleName()));
	if(inspectable instanceof IFocusable selectable) {
	    var btn = new Button("FOCUS");
	    btn.setOnAction(e -> selectable.focus());
	    inspectors.add(btn);
	}
	for(var p : inspectableProperties) {
	    var group = new VBox();
	    group.getChildren().add(new Label(p.name()));
	    group.getChildren().add(InspectorUtils.getObservableInspector(p.property()));
	    inspectors.add(group);
	}
	propertiesContainer.getChildren().addAll(inspectors);
    }
}

