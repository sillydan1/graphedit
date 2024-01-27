package dk.gtz.graphedit.plugins.view;

import java.util.ArrayList;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.IInspectable;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelSelection;
import dk.yalibs.yadi.DI;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The javafx controller for the syntax property inspector panel
 */
public class InspectorController extends StackPane {
    private final VBox bufferPropertiesContainer;
    private final VBox propertiesContainer;
    private final ScrollPane scrollPane;
    private final ObservableList<ViewModelSelection> selectedElements;
    private final IBufferContainer openBuffers;

    public InspectorController() {
	openBuffers = DI.get(IBufferContainer.class);
	selectedElements = DI.get("selectedElements");
	var padding = new Insets(10);
	bufferPropertiesContainer = new VBox();
	bufferPropertiesContainer.setSpacing(5);
	bufferPropertiesContainer.setPadding(padding);
	propertiesContainer = new VBox();
	propertiesContainer.setSpacing(5);
	propertiesContainer.setPadding(padding);
	var seperator = new Separator(Orientation.HORIZONTAL);
	seperator.setPadding(padding);
	scrollPane = new ScrollPane(new VBox(
		    bufferPropertiesContainer,
		    seperator,
		    propertiesContainer));
	scrollPane.setFitToWidth(true);
	initialize();
    }

    private void initialize() {
	getChildren().add(scrollPane);
	addAllOpenBuffers();
	addAllSelected();
	initializeSelectionEventHandlers();
    }

    private void addAllOpenBuffers() {
	for(var buffer : openBuffers.getBuffers().entrySet()) {
	    var inspectors = new ArrayList<Node>();
	    inspectors.add(declarationsEditor(buffer.getValue().syntax().declarations()));
	    inspectors.add(metadataEditor(buffer.getValue().metadata()));
	    var box = new VBox();
	    box.getChildren().addAll(inspectors);
	    var pane = new TitledPane(buffer.getKey(), box);
	    pane.setExpanded(false);
	    bufferPropertiesContainer.getChildren().add(pane);
	}
    }

    private Node declarationsEditor(StringProperty declarations) {
	var label = new Label("Declarations");
	var inspector = InspectorUtils.getPropertyInspector(declarations);
	var pane = new BorderPane();
	BorderPane.setAlignment(label, Pos.CENTER);
	BorderPane.setAlignment(inspector, Pos.CENTER);
	pane.setCenter(inspector);
	pane.setTop(label);
	return pane;
    }

    private Node metadataEditor(MapProperty<String,String> metadata) {
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.getStyleClass().add(Styles.SUCCESS);
	addButton.setOnAction(e -> metadata.put("KEY", "VALUE"));
	var box = new HBox(addButton);
	box.setPadding(new Insets(5));
	box.setAlignment(Pos.CENTER);
	var inspector = new VBox(createMapTextEditorNode(metadata), box);
	var bp = new BorderPane();
	var label = new Label("Metadata");
	BorderPane.setAlignment(label, Pos.CENTER);
	BorderPane.setAlignment(inspector, Pos.CENTER);
	bp.setTop(label);
	bp.setCenter(inspector);
	return bp;
    }

    private Node createMapTextEditorNode(MapProperty<String,String> list) {
	var listView = new VBox();
	listView.setSpacing(5);
	list.addListener((e,o,n) -> updateMapListView(listView, list));
	updateMapListView(listView, list);
	return listView;
    }

    private void updateMapListView(VBox view, MapProperty<String,String> list) {
	view.getChildren().clear();
	for(var element : list.entrySet()) {
	    var key = new SimpleStringProperty(element.getKey());
	    var keyed = new TextField(element.getKey());
	    keyed.focusedProperty().addListener((e,o,n) -> {
		if(n)
		    return;
		var newVal = keyed.getText();
		var oldVal = list.get(key.getValue());
		list.remove(key.getValue());
		list.put(newVal, oldVal);
		key.setValue(newVal);
	    });
	    var valed = new TextField(element.getValue());
	    valed.focusedProperty().addListener((e,o,n) -> {
		if(n)
		    return;
		list.put(key.getValue(), valed.getText());
	    });
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.getStyleClass().add(Styles.DANGER);
	    removeButton.setOnAction(e -> list.remove(element.getKey()));
	    var box = new HBox(removeButton, keyed, valed);
	    box.setSpacing(5);
	    view.getChildren().add(box);
	}
    }

    private void addAllSelected() {
	for(var element : selectedElements)
	    if(element.selectable() instanceof IInspectable inspectable)
		addInspectable(inspectable);
    }

    private void initializeSelectionEventHandlers() {
	selectedElements.addListener((ListChangeListener<ViewModelSelection>)(n) -> {
	    propertiesContainer.getChildren().clear();
	    addAllSelected();
	});
	openBuffers.getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
	    bufferPropertiesContainer.getChildren().clear();
	    addAllOpenBuffers();
	});
    }

    private void addInspectable(IInspectable inspectable) {
	var inspectableProperties = inspectable.getInspectableObjects();
	var inspectors = new ArrayList<Node>();
	for(var p : inspectableProperties) {
	    var box = new BorderPane();
	    var inspector = InspectorUtils.getObservableInspector(p.property());
	    var label = new Label(p.name());
	    BorderPane.setAlignment(label, Pos.CENTER);
	    BorderPane.setAlignment(inspector, Pos.CENTER);
	    box.setLeft(label);
	    box.setRight(inspector);
	    inspectors.add(box);
	}
	var box = new VBox();
	box.setSpacing(5);
	box.getChildren().addAll(inspectors);
	var pane = new TitledPane(inspectable.getClass().getSimpleName(), box);
	pane.getStyleClass().add(Styles.DENSE);
	propertiesContainer.getChildren().add(pane);
    }
}
