package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.Tile;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.ViewModelEnvironmentVariable;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
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
	initRunTargetsList();
	initAddButton();
	initRemoveButton();
	var selectedRunTarget = getSelectedViewModelRunTarget();
	if(selectedRunTarget.isPresent())
	    renderRunTargetInspector(selectedRunTarget.get());
    }

    private void initAddButton() {
	addButton.setGraphic(new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.setOnAction(e -> project.runTargets().add(new ViewModelRunTarget("default", "ls", ".", true, true, new ArrayList<String>(), new HashMap<String,String>())));
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
	var tile = new Tile("Name", "The name of the runtarget");
	var nameInspector = InspectorUtils.getPropertyInspectorField(runTarget.name());
	tile.setAction(nameInspector);
	tile.setActionHandler(nameInspector::requestFocus);
	inspectorPane.getChildren().add(tile);

	var commandTile = new Tile("Command", "The executable to run");
	var commandInspector = InspectorUtils.getPropertyInspectorField(runTarget.command());
	commandTile.setAction(commandInspector);
	commandTile.setActionHandler(commandInspector::requestFocus);
	inspectorPane.getChildren().add(commandTile);

	var cwdTile = new Tile("Working Directory", "The directory context to execute the runtarget in");
	var cwdInspector = InspectorUtils.getPropertyInspectorField(runTarget.currentWorkingDirectory());
	cwdTile.setAction(cwdInspector);
	cwdTile.setActionHandler(cwdInspector::requestFocus);
	inspectorPane.getChildren().add(cwdTile);

	var shellTile = new Tile("Run as shell", "Whether to run the runtarget as a shell command");
	var shellInspector = InspectorUtils.getObservableInspector(runTarget.runAsShell());
	shellTile.setAction(shellInspector);
	if(shellInspector instanceof ToggleSwitch ts)
	    shellTile.setActionHandler(ts::fire);
	inspectorPane.getChildren().add(shellTile);

	var runBeforeTile = new Tile("Save before run", "Whether to save the model before running this command");
	var runBeforeInspector = InspectorUtils.getObservableInspector(runTarget.runAsShell());
	runBeforeTile.setAction(runBeforeInspector);
	if(runBeforeInspector instanceof ToggleSwitch ts)
	    runBeforeTile.setActionHandler(ts::fire);
	inspectorPane.getChildren().add(runBeforeTile);

	var argAddButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	argAddButton.getStyleClass().add(Styles.SUCCESS);
	argAddButton.setOnAction(e -> runTarget.arguments().add(new SimpleStringProperty()));
	var argBox = new HBox(argAddButton);
	argBox.setPadding(new Insets(5));
	argBox.setAlignment(Pos.CENTER);
	var argTableVBox = new VBox(argBox, createListTextEditorNode(runTarget.arguments()));
	var argTile = new Tile("Arguments", "Set arguments provided to the command");
	argTile.setAction(argTableVBox);
	inspectorPane.getChildren().add(argTile);

	var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
	addButton.getStyleClass().add(Styles.SUCCESS);
	addButton.setOnAction(e -> runTarget.environment().add(new ViewModelEnvironmentVariable(new SimpleStringProperty("KEY"), new SimpleStringProperty("VALUE"))));
	var box = new HBox(addButton);
	box.setPadding(new Insets(5));
	box.setAlignment(Pos.CENTER);
	var tableVBox = new VBox(box, createMapTextEditorNode(runTarget.environment()));
	var environtmentTile = new Tile("ENV", "Set extra environment variables for the command");
	environtmentTile.setAction(tableVBox);
	inspectorPane.getChildren().add(environtmentTile);

	var runButton = new Button("Run", new FontIcon(BootstrapIcons.PLAY));
	runButton.getStyleClass().add(Styles.ACCENT);
	runButton.setOnAction(e -> EditorActions.executeRunTarget(runTarget));
	var saveButton = new Button("Save");
	saveButton.setOnAction(e -> EditorActions.saveProject());
	var saveBox = new HBox(saveButton, runButton);
	saveBox.setSpacing(5);
	saveBox.setAlignment(Pos.CENTER);
	inspectorPane.getChildren().addAll(new Separator(), saveBox);
    }

    private Node createListTextEditorNode(ListProperty<StringProperty> list) {
	var listView = new VBox();
	listView.setSpacing(5);
	list.addListener((e,o,n) -> updateListView(listView, list));
	updateListView(listView, list);
	return listView;
    }

    private void updateListView(VBox view, ListProperty<StringProperty> list) {
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

    private Node createMapTextEditorNode(ListProperty<ViewModelEnvironmentVariable> list) {
	var listView = new VBox();
	listView.setSpacing(5);
	list.addListener((e,o,n) -> updateMapListView(listView, list));
	updateMapListView(listView, list);
	return listView;
    }

    private void updateMapListView(VBox view, ListProperty<ViewModelEnvironmentVariable> list) {
	view.getChildren().clear();
	for(var element : list) {
	    var keyed = new TextField(element.key().get());
	    keyed.textProperty().bindBidirectional(element.key());
	    var valed = new TextField(element.value().get());
	    valed.textProperty().bindBidirectional(element.value());
	    var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
	    removeButton.getStyleClass().add(Styles.DANGER);
	    removeButton.setOnAction(e -> list.remove(element));
	    var box = new HBox(removeButton, keyed, valed);
	    box.setSpacing(5);
	    view.getChildren().add(box);
	}
    }
}

