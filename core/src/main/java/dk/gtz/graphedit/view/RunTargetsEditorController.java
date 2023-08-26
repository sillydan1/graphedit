package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RunTargetsEditorController {
    private static Logger logger = LoggerFactory.getLogger(RunTargetsEditorController.class);
    private static class RunTargetViewNode extends Label {
	private ViewModelRunTarget runTarget;

	public RunTargetViewNode(ViewModelRunTarget runTarget) {
	    super(runTarget.name().get());
	    this.runTarget = runTarget;
	    textProperty().bind(runTarget.name());
	}

	public ViewModelRunTarget getRunTarget() {
	    return runTarget;
	}
    }

    @FXML
    private BorderPane root;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private ListView<RunTargetViewNode> runTargetsOverviewList;
    @FXML
    private VBox inspectorPane;
    private ViewModelProject project;

    @FXML
    private void initialize() {
	project = DI.get(ViewModelProject.class);
	initRoot();
	initRunTargetsList();
	initAddButton();
	initRemoveButton();
	var selectedRunTarget = getSelectedViewModelRunTarget();
	if(selectedRunTarget.isPresent())
	    renderRunTargetInspector(selectedRunTarget.get());
    }

    private void initRoot() {
	root.getStyleClass().add(Styles.BG_DEFAULT);
    }

    private void initAddButton() {
	addButton.setGraphic(new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> project.runTargets().add(new ViewModelRunTarget("default", "ls", ".", true, new ArrayList<String>(), new HashMap<String,String>())));
    }

    private void initRemoveButton() {
	removeButton.setGraphic(new FontIcon(BootstrapIcons.X_CIRCLE));
	removeButton.setOnAction(e -> {
	    var selectedRunTarget = getSelectedViewModelRunTarget();
	    if(selectedRunTarget.isEmpty())
		return;
	    var didRemove = project.runTargets().remove(selectedRunTarget.get());
	    if(!didRemove)
		logger.warn("Could not remove '%s' RunTarget from list".formatted(selectedRunTarget.get().name()));
	    selectFirstElement();
	});
    }

    private Optional<ViewModelRunTarget> getSelectedViewModelRunTarget() {
	var item = runTargetsOverviewList.getSelectionModel().getSelectedItem();
	if(item == null)
	    return Optional.empty();
	return Optional.of(item.getRunTarget());
    }

    private void selectFirstElement() {
	runTargetsOverviewList.getSelectionModel().selectFirst();
    }

    private void initRunTargetsList() {
	updateRunTargetsOverview();
	project.runTargets().addListener((e,o,n) -> updateRunTargetsOverview());
	runTargetsOverviewList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	runTargetsOverviewList.getSelectionModel().selectedItemProperty().addListener((e,o,n) -> {
	    inspectorPane.getChildren().clear();
	    if(n != null)
		renderRunTargetInspector(n.getRunTarget());
	});
    }

    private void updateRunTargetsOverview() {
	runTargetsOverviewList.getItems().clear();
	for(var runTarget : project.runTargets())
	    runTargetsOverviewList.getItems().add(new RunTargetViewNode(runTarget));
	selectFirstElement();
    }

    private void renderRunTargetInspector(ViewModelRunTarget runTarget) {
	inspectorPane.getChildren().add(createTextEditorNode("name", runTarget.name()));
	inspectorPane.getChildren().add(createTextEditorNode("command", runTarget.command()));
	inspectorPane.getChildren().add(createTextEditorNode("cwd", runTarget.currentWorkingDirectory()));
	inspectorPane.getChildren().add(createBooleanEditorNode("runAsShell", runTarget.runAsShell()));
	inspectorPane.getChildren().add(new HBox(new Label("arguments"), createListTextEditorNode(runTarget.arguments())));
	inspectorPane.getChildren().add(new HBox(new Label("environment"), createMapTextEditorNode(runTarget.environment())));
    }

    private Node createBooleanEditorNode(String labelText, BooleanProperty property) {
	var returnValue = new ToggleSwitch(labelText);
	returnValue.setSelected(property.get());
	property.bind(returnValue.selectedProperty());
	return returnValue;
    }

    private Node createTextEditorNode(String labelText, StringProperty property) {
	var returnValue = new TextField(property.get());
	property.bind(returnValue.textProperty());
	return new HBox(new Label(labelText), returnValue);
    }

    private Node createListTextEditorNode(ListProperty<StringProperty> list) {
	var listView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> list.add(new SimpleStringProperty()));
	list.addListener((e,o,n) -> updateListView(listView, list));
	updateListView(listView, list);
	return new VBox(addButton, listView);
    }

    private void updateListView(VBox view, ListProperty<StringProperty> list) {
	view.getChildren().clear();
	for(var element : list) {
	    var ed = new TextField(element.get());
	    element.bind(ed.textProperty());
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> list.remove(element));
	    view.getChildren().add(new HBox(removeButton, ed));
	}
    }

    private Node createMapTextEditorNode(MapProperty<StringProperty,StringProperty> map) {
	var listView = new VBox();
	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> map.put(new SimpleStringProperty(""), new SimpleStringProperty("")));
	map.addListener((e,o,n) -> updateMapListView(listView, map));
	updateMapListView(listView, map);
	return new VBox(addButton, listView);
    }

    private void updateMapListView(VBox view, MapProperty<StringProperty,StringProperty> map) {
	view.getChildren().clear();
	for(var element : map.entrySet()) {
	    var keyed = new TextField(element.getKey().get());
	    element.getKey().bind(keyed.textProperty());
	    var valed = new TextField(element.getValue().get());
	    element.getValue().bind(valed.textProperty());
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.setOnAction(e -> map.remove(element.getKey(), element.getValue()));
	    view.getChildren().add(new HBox(removeButton, keyed, valed));
	}
    }
}

