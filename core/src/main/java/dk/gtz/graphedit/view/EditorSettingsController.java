package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.Tile;
import atlantafx.base.controls.ToggleSwitch;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.view.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EditorSettingsController {
    private static final Logger logger = LoggerFactory.getLogger(EditorSettingsController.class);
    @FXML
    private VBox inspectorPane;
    private ViewModelEditorSettings editorSettings;

    public void initialize() {
	editorSettings = DI.get(ViewModelEditorSettings.class);
	addInspector("Use GridSnap", "Makes vertices snap to the background grid", editorSettings.gridSnap());
	addInspector("GridSnap XSize", "", editorSettings.gridSizeX());
	addInspector("GridSnap YSize", "", editorSettings.gridSizeY());
	addInspector("Light Theme", "Use light theme", editorSettings.useLightTheme());
	addInspector("Auto Open", "Automatically open the project you closed last", editorSettings.autoOpenLastProject());
	addSaveButton();
    }

    private void addInspector(String labelName, String description, Observable observable) {
	var inspector = InspectorUtils.getObservableInspector(observable);
	var tile = new Tile(labelName, description);
	tile.setAction(inspector);
	if(inspector instanceof ToggleSwitch ts)
	    tile.setActionHandler(ts::fire);
	inspectorPane.getChildren().add(tile);
    }

    private void addSaveButton() {
	var saveButton = new Button("Save Changes");
	saveButton.setOnAction((e) -> EditorActions.saveEditorSettings(editorSettings));
	var pane = new HBox(saveButton);
	pane.setAlignment(Pos.CENTER);
	inspectorPane.getChildren().addAll(new Separator(), pane);
    }
}

