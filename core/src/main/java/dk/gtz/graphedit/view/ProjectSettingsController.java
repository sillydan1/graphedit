package dk.gtz.graphedit.view;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * Project settings editor view controller.
 */
public class ProjectSettingsController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSettingsController.class);
	@FXML
	private VBox inspectorPane;
	private ViewModelProject settings;

	/**
	 * Construct a new instance
	 */
	public ProjectSettingsController() {

	}

	@FXML
	private void initialize() {
		settings = DI.get(ViewModelProject.class);
		var tile = new Tile("Name", "The name of the project");
		var nameInspector = InspectorUtils.getPropertyInspectorField(settings.name());
		tile.setAction(nameInspector);
		tile.setActionHandler(nameInspector::requestFocus);
		inspectorPane.getChildren().add(tile);

		var argAddButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
		argAddButton.getStyleClass().add(Styles.SUCCESS);
		argAddButton.setOnAction(e -> settings.excludeFiles().add(new SimpleStringProperty()));
		var argBox = new HBox(argAddButton);
		argBox.setPadding(new Insets(5));
		argBox.setAlignment(Pos.CENTER);
		var argTableVBox = new VBox(argBox, InspectorUtils.getPropertyInspectorList(settings.excludeFiles()));
		var argTile = new Tile("Exclude files", "Set explicit files to exclude");
		argTile.setAction(argTableVBox);
		inspectorPane.getChildren().add(argTile);

		var addButton = new Button("Add", new FontIcon(BootstrapIcons.PLUS_CIRCLE));
		addButton.getStyleClass().add(Styles.SUCCESS);
		addButton.setOnAction(e -> settings.metadata()
				.add(new Pair<>(new SimpleStringProperty("KEY"), new SimpleStringProperty("VALUE"))));
		var box = new HBox(addButton);
		box.setPadding(new Insets(5));
		box.setAlignment(Pos.CENTER);
		var tableVBox = new VBox(box, createMapTextEditorNode(settings.metadata()));
		var environtmentTile = new Tile("Metadata", "Data useful for external tools");
		environtmentTile.setAction(tableVBox);
		inspectorPane.getChildren().add(environtmentTile);

		addButton("Edit RunTargets", "Open the RunTargets editor modal", EditorActions::openRunTargetsEditor);

		addSaveButton();
	}

	private void addButton(String labelName, String description, Runnable action) {
		var tile = new Tile(labelName, description);
		tile.setActionHandler(action);
		inspectorPane.getChildren().add(tile);
	}

	private Node createMapTextEditorNode(ListProperty<Pair<StringProperty, StringProperty>> list) {
		var listView = new VBox();
		listView.setSpacing(5);
		list.addListener((e, o, n) -> updateMapListView(listView, list));
		updateMapListView(listView, list);
		return listView;
	}

	private void updateMapListView(VBox view, ListProperty<Pair<StringProperty, StringProperty>> list) {
		view.getChildren().clear();
		for (var element : list) {
			var keyed = new TextField(element.getKey().get());
			keyed.textProperty().bindBidirectional(element.getKey());
			var valed = new TextField(element.getValue().get());
			valed.textProperty().bindBidirectional(element.getValue());
			var removeButton = new Button(null, new FontIcon(BootstrapIcons.X_CIRCLE));
			removeButton.getStyleClass().add(Styles.DANGER);
			removeButton.setOnAction(e -> list.remove(element));
			var box = new HBox(removeButton, keyed, valed);
			box.setSpacing(5);
			view.getChildren().add(box);
		}
	}

	private void addSaveButton() {
		var saveButton = new Button("Save Changes");
		saveButton.setOnAction(e -> EditorActions.saveProject());
		var pane = new HBox(saveButton);
		pane.setAlignment(Pos.CENTER);
		inspectorPane.getChildren().addAll(new Separator(), pane);
	}
}
