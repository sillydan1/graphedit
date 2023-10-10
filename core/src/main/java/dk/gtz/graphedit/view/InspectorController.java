package dk.gtz.graphedit.view;

import java.util.ArrayList;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.view.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.gtz.graphedit.viewmodel.IInspectable;
import dk.gtz.graphedit.viewmodel.ViewModelSelection;
import dk.yalibs.yadi.DI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The javafx controller for the syntax property inspector panel
 */
public class InspectorController extends StackPane {
    @FXML
    private VBox propertiesContainer;
    @FXML
    private ScrollPane scrollPane;
    private final ObservableList<ViewModelSelection> selectedElements;

    public InspectorController() {
	selectedElements = DI.get("selectedElements");
    }

    @FXML
    private void initialize() {
	propertiesContainer.setSpacing(5);
	propertiesContainer.setPadding(new Insets(10));
	scrollPane.setContent(propertiesContainer);
	addAllSelected();
	initializeSelectionEventHandlers();
    }

    private void addAllSelected() {
	for(var element : selectedElements) {
	    if(element.selectable() instanceof IInspectable inspectable)
		addInspectable(inspectable);
	}
    }

    private void initializeSelectionEventHandlers() {
	selectedElements.addListener((ListChangeListener<ViewModelSelection>)(n) -> {
	    propertiesContainer.getChildren().clear();
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
	inspectors.add(new Separator());
	propertiesContainer.getChildren().addAll(inspectors);
    }
}

