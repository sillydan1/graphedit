package dk.gtz.graphedit.view;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ser.std.MapProperty;

import atlantafx.base.controls.ToggleSwitch;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.viewmodel.IInspectable;
import dk.gtz.graphedit.viewmodel.ISelectable;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
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
	for(var element : selectedElements)
	    if(element instanceof IInspectable inspectable)
		addInspectable(inspectable);
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
	for(var p : inspectableProperties) {
	    var group = new Group();
	    group.getChildren().add(new Label(p.name()));
	    group.getChildren().add(getObservableInspector(p.property()));
	    inspectors.add(group);
	}
	propertiesContainer.getChildren().addAll(inspectors);
    }

    private Node getObservableInspector(Observable o) {
	if(o instanceof BooleanProperty p) return getPropertyInspector(p);
	if(o instanceof DoubleProperty p) return getPropertyInspector(p);
	if(o instanceof FloatProperty p) return getPropertyInspector(p);
	if(o instanceof IntegerProperty p) return getPropertyInspector(p);
	if(o instanceof ListProperty p) return getPropertyInspector(p);
	if(o instanceof LongProperty p) return getPropertyInspector(p);
	if(o instanceof MapProperty p) return getPropertyInspector(p);
	if(o instanceof ObjectProperty p) return getPropertyInspector(p);
	if(o instanceof SetProperty p) return getPropertyInspector(p);
	if(o instanceof StringProperty p) return getPropertyInspector(p);
	throw new RuntimeException("No such property inspector implemented for type '%s'".formatted(o.getClass().getSimpleName()));
    }

    private Node getPropertyInspector(ObjectProperty property) {
	return new Label("unsupported type"); // TODO: Implement this
    }

    private Node getPropertyInspector(MapProperty property) {
	return new Label("unsupported type"); // TODO: Implement this
    }

    private Node getPropertyInspector(ListProperty property) {
	return new Label("unsupported type"); // TODO: Implement this
    }

    private Node getPropertyInspector(SetProperty property) {
	return new Label("unsupported type"); // TODO: Implement this
    }

    private Node getPropertyInspector(StringProperty property) {
	return new Label(property.get()); // TODO: This should be a TextField
    }

    private Node getPropertyInspector(BooleanProperty property) {
	var s = new ToggleSwitch(property.getName());
	s.setSelected(property.get());
	s.selectedProperty().addListener((e,o,n) -> property.set(n));
	return s;
    }

    private Node getPropertyInspector(IntegerProperty property) {
	var s = new Spinner<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }
    
    private Node getPropertyInspector(LongProperty property) {
	var s = new Spinner<Long>(Long.MIN_VALUE, Long.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }

    private Node getPropertyInspector(FloatProperty property) {
	var s = new Spinner<Float>(Float.MIN_VALUE, Float.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }

    private Node getPropertyInspector(DoubleProperty property) {
	var s = new Spinner<Double>(Double.MIN_VALUE, Double.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }
}

