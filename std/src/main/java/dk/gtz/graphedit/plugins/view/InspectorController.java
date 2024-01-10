package dk.gtz.graphedit.plugins.view;

import java.util.ArrayList;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.gtz.graphedit.viewmodel.IInspectable;
import dk.gtz.graphedit.viewmodel.ViewModelSelection;
import dk.yalibs.yadi.DI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The javafx controller for the syntax property inspector panel
 */
public class InspectorController extends StackPane {
    private final Accordion propertiesContainer;
    private final ScrollPane scrollPane;
    private final ObservableList<ViewModelSelection> selectedElements;

    public InspectorController() {
	selectedElements = DI.get("selectedElements");
	propertiesContainer = new Accordion();
	propertiesContainer.setPadding(new Insets(10));
	scrollPane = new ScrollPane(propertiesContainer);
	scrollPane.setFitToWidth(true);
	initialize();
    }

    private void initialize() {
	getChildren().add(scrollPane);
	addAllSelected();
	initializeSelectionEventHandlers();
    }

    private void addAllSelected() {
	for(var element : selectedElements)
	    if(element.selectable() instanceof IInspectable inspectable)
		addInspectable(inspectable);
    }

    private void initializeSelectionEventHandlers() {
	selectedElements.addListener((ListChangeListener<ViewModelSelection>)(n) -> {
	    propertiesContainer.getPanes().clear();
	    addAllSelected();
	});
    }

    private void addInspectable(IInspectable inspectable) {
	var inspectableProperties = inspectable.getInspectableObjects();
	var inspectors = new ArrayList<Node>();
	var typeLabel = new Label(inspectable.getClass().getSimpleName());
	typeLabel.getStyleClass().add(Styles.TITLE_3);
	if(inspectable instanceof IFocusable selectable) {
	    typeLabel.setOnMouseClicked(e -> selectable.focus());
	    typeLabel.setCursor(Cursor.HAND);
	}
	inspectors.add(typeLabel);
	for(var p : inspectableProperties) {
	    var group = new HBox();
	    group.setSpacing(10);
	    var label = new Label(p.name());
	    var inspector = InspectorUtils.getObservableInspector(p.property());
	    label.setAlignment(Pos.CENTER_RIGHT);
	    group.getChildren().addAll(inspector, label);
	    inspectors.add(group);
	}
	var box = new VBox();
	box.getChildren().addAll(inspectors);
	var pane = new TitledPane(inspectable.getClass().getSimpleName(), box);
	propertiesContainer.getPanes().add(pane);
    }
}

