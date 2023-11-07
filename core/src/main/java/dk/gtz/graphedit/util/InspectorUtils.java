package dk.gtz.graphedit.util;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SetProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * General utilities for manipulating and creating {@link Property} inspectors / editors.
 */
public class InspectorUtils {
    /**
     * General inspector creator function, use if you dont know what concrete type of {@link Observable} you have.
     * @param o the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link Observable}.
     * @throws RuntimeException if the provided {@link Observable} is not supported
     * @throws ClassCastException if the provided {@link Observable} is a collection type of non-observables
     */
    public static Node getObservableInspector(Observable o) {
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

    public static Node getPropertyInspectorList(ListProperty<StringProperty> list) {
	var listView = new VBox();
	listView.setSpacing(5);
	list.addListener((e,o,n) -> updateTextListView(listView, list));
	updateTextListView(listView, list);
	return listView;
    }

    /**
     * Get an inspector for a generic object.
     *
     * Note that at the time of writing, the resulting inspector cannot change anything about the object.
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link ObjectProperty}.
     */
    public static Node getPropertyInspector(ObjectProperty<?> property) {
	if(property.get() == null)
	    return new Label("null object");
	return new Label(property.get().getClass().getSimpleName());
    }

    /**
     * Get an inspector for a map of observales
     *
     * Note that this provides an {@link HBox} with the name of the property on the left.
     * If you just want the map inspector, see {@link #createMapTextEditorNode(MapProperty)} instead.
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link MapProperty}
     * @see #createMapTextEditorNode(MapProperty)
     */
    public static Node getPropertyInspector(MapProperty<? extends Observable, ? extends Observable> property) {
	return new HBox(new Label(property.getName(), createMapTextEditorNode(property)));
    }

    /**
     * Get an inspector for a map of observables
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link MapProperty}
     */
    public static Node createMapTextEditorNode(MapProperty<? extends Observable, ? extends Observable> property) {
	var listView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> property.put(null, null)); // TODO: nulls here... hmmm...
	property.addListener((e,o,n) -> updateMapListView(listView, property));
	updateMapListView(listView, property);
	return new VBox(addButton, listView);
    }

    private static void updateMapListView(VBox view, MapProperty<? extends Observable, ? extends Observable> property) {
	view.getChildren().clear();
	for(var element : property.entrySet()) {
	    var keyed = getObservableInspector(element.getKey());
	    var valed = getObservableInspector(element.getValue());
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> property.remove(element.getKey(), element.getValue()));
	    view.getChildren().add(new HBox(removeButton, keyed, valed));
	}
    }

    /**
     * Get an inspector for a list of observables
     *
     * Note that this provides an {@link HBox} with the name of the property on the left.
     * If you just want the list inspector, see {@link #createListEditorNode(ListProperty)} instead.
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link ListProperty}
     * @see #createListEditorNode(ListProperty)
     */
    public static Node getPropertyInspector(ListProperty<? extends Observable> property) {
	return new HBox(new Label(property.getName()), createListEditorNode(property));
    }

    /**
     * Get an inspector for a list of observables
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link ListProperty}
     */
    public static Node createListEditorNode(ListProperty<? extends Observable> property) {
	var listView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> property.add(null)); // hmm...
	property.addListener((e,o,n) -> updateListView(listView, property));
	updateListView(listView, property);
	return new VBox(addButton, listView);
    }

    private static void updateTextListView(VBox view, ListProperty<StringProperty> list) {
	view.getChildren().clear();
	for(var element : list) {
	    var ed = new TextField(element.get());
	    element.bind(ed.textProperty());
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.getStyleClass().add(Styles.DANGER);
	    removeButton.setOnAction(e -> list.remove(element));
	    var box = new HBox(removeButton, ed);
	    box.setSpacing(5);
	    view.getChildren().add(box);
	}
    }

    private static void updateListView(VBox view, ListProperty<? extends Observable> property) {
	view.getChildren().clear();
	for(var element : property) {
	    var ed = getObservableInspector(element);
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> property.remove(element));
	    view.getChildren().add(new HBox(removeButton, ed));
	}
    }

    /**
     * Get an inspector for a set of obseravables
     *
     * Note that this provides an {@link HBox} with the name of the property on the left.
     * If you just want the set inspector, see {@link #createSetEditorNode(SetProperty)} instead.
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link SetProperty}
     * @see #createSetEditorNode(SetProperty)
     */
    public static Node getPropertyInspector(SetProperty<? extends Observable> property) {
	return new HBox(new Label(property.getName()), createSetEditorNode(property));
    }

    /**
     * Get an inspector for a set of observables
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link SetProperty}
     */
    public static Node createSetEditorNode(SetProperty<? extends Observable> property) {
	var setView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> property.add(null)); // hmm...
	property.addListener((e,o,n) -> updateSetView(setView, property));
	updateSetView(setView, property);
	return new VBox(addButton, setView);
    }

    private static void updateSetView(VBox view, SetProperty<? extends Observable> property) {
	view.getChildren().clear();
	for(var element : property) {
	    var ed = getObservableInspector(element);
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> property.remove(element));
	    view.getChildren().add(new HBox(removeButton, ed));
	}
    }

    /**
     * Get an inspector for a string property
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link StringProperty}
     */
    public static Node getPropertyInspector(StringProperty property) {
	var result = new TextArea(property.get());
	result.textProperty().bindBidirectional(property);
	return result;
    }

    /**
     * Get an inspector for a string property, but instead of a TextArea, it gives a TextField
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link StringProperty}
     */
    public static TextField getPropertyInspectorField(StringProperty property) {
	var result = new TextField(property.get());
	result.textProperty().bindBidirectional(property);
	return result;
    }

    /**
     * Get an inspector for a boolean property
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link BooleanProperty}
     */
    public static Node getPropertyInspector(BooleanProperty property) {
	var s = new ToggleSwitch(property.getName());
	s.setSelected(property.get());
	s.selectedProperty().addListener((e,o,n) -> property.set(n));
	return s;
    }

    /**
     * Get an inspector for an integer property
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link IntegerProperty}
     */
    public static Node getPropertyInspector(IntegerProperty property) {
	var s = new Spinner<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }
    
    /**
     * Get an inspector for a long property
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link LongProperty}
     */
    public static Node getPropertyInspector(LongProperty property) {
	var s = new Spinner<Long>(Long.MIN_VALUE, Long.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }

    /**
     * Get an inspector for a float property
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link FloatProperty}
     */
    public static Node getPropertyInspector(FloatProperty property) {
	var s = new Spinner<Float>(Float.MIN_VALUE, Float.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }

    /**
     * Get an inspector for a double property
     * @param property the thing that the inspector modifies
     * @return a javafx component that can modify the value of the provided {@link DoubleProperty}
     */
    public static Node getPropertyInspector(DoubleProperty property) {
	var s = new Spinner<Double>(Double.MIN_VALUE, Double.MAX_VALUE, property.get());
	s.valueProperty().addListener((e,o,n) -> property.set(n));
	s.setEditable(true);
	s.setPrefWidth(120);
	return s;
    }
}
