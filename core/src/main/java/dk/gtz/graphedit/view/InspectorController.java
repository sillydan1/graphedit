package dk.gtz.graphedit.view;

import java.util.ArrayList;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.ToggleSwitch;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.gtz.graphedit.viewmodel.IInspectable;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.yalibs.yadi.DI;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
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
	if(o instanceof LongProperty p) return getPropertyInspector(p);
	if(o instanceof StringProperty p) return getPropertyInspector(p);
	if(o instanceof ObjectProperty<?> p) return getPropertyInspector(p);
	// NOTE: Intentional classcast exception here. We cannot safely cast to generic types because java is stupid
	if(o instanceof ListProperty p) return getPropertyInspector(p);
	if(o instanceof MapProperty p) return getPropertyInspector(p);
	if(o instanceof SetProperty p) return getPropertyInspector(p);
	throw new RuntimeException("No such property inspector implemented for type '%s'".formatted(o.getClass().getSimpleName()));
    }

    private Node getPropertyInspector(ObjectProperty<?> property) {
	if(property.get() == null)
	    return new Label("null object");
	return new Label(property.get().getClass().getSimpleName());
    }

    private Node getPropertyInspector(MapProperty<? extends Observable, ? extends Observable> property) {
	return new HBox(new Label(property.getName(), createMapTextEditorNode(property)));
    }

    private Node createMapTextEditorNode(MapProperty<? extends Observable, ? extends Observable> map) {
	var listView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> map.put(null, null)); // hmmm...
	map.addListener((e,o,n) -> updateMapListView(listView, map));
	updateMapListView(listView, map);
	return new VBox(addButton, listView);
    }

    private void updateMapListView(VBox view, MapProperty<? extends Observable, ? extends Observable> map) {
	view.getChildren().clear();
	for(var element : map.entrySet()) {
	    var keyed = getObservableInspector(element.getKey());
	    var valed = getObservableInspector(element.getValue());
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> map.remove(element.getKey(), element.getValue()));
	    view.getChildren().add(new HBox(removeButton, keyed, valed));
	}
    }

    private Node getPropertyInspector(ListProperty<? extends Observable> property) {
	return new HBox(new Label(property.getName()), createListEditorNode(property));
    }

    private Node createListEditorNode(ListProperty<? extends Observable> list) {
	var listView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> list.add(null)); // hmm...
	list.addListener((e,o,n) -> updateListView(listView, list));
	updateListView(listView, list);
	return new VBox(addButton, listView);
    }

    private void updateListView(VBox view, ListProperty<? extends Observable> list) {
	view.getChildren().clear();
	for(var element : list) {
	    var ed = getObservableInspector(element);
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> list.remove(element));
	    view.getChildren().add(new HBox(removeButton, ed));
	}
    }

    private Node getPropertyInspector(SetProperty<? extends Observable> property) {
	return new HBox(new Label(property.getName()), createSetEditorNode(property));
    }

    private Node createSetEditorNode(SetProperty<? extends Observable> set) {
	var setView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> set.add(null)); // hmm...
	set.addListener((e,o,n) -> updateSetView(setView, set));
	updateSetView(setView, set);
	return new VBox(addButton, setView);
    }

    private void updateSetView(VBox view, SetProperty<? extends Observable> set) {
	view.getChildren().clear();
	for(var element : set) {
	    var ed = getObservableInspector(element);
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> set.remove(element));
	    view.getChildren().add(new HBox(removeButton, ed));
	}
    }

    private Node getPropertyInspector(StringProperty property) {
	var result = new TextArea(property.get());
	result.textProperty().bindBidirectional(property);
	return result;
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

