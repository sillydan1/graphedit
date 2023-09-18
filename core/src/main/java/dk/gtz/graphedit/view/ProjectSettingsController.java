package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.Tile;
import atlantafx.base.controls.ToggleSwitch;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.view.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class ProjectSettingsController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectSettingsController.class);
    @FXML
    private VBox inspectorPane;
    private ViewModelProject settings;

    public void initialize() {
	settings = DI.get(ViewModelProject.class);
	addInspector("Project Name", "The name of the project", settings.name());
	addInspector("Exclude Files", "Files to exclude from the project", settings.excludeFiles());
	addInspector("Metadata", "Metadata fields, used for external tooling", settings.metadata());
	inspectorPane.getChildren().add(new Separator());
	addButton("Edit RunTargets", "Open the RunTargets editor modal", EditorActions::openRunTargetsEditor);
    }

    private void addInspector(String labelName, String description, Observable observable) {
	var inspector = InspectorUtils.getObservableInspector(observable);
	var tile = new Tile(labelName, description);
	tile.setAction(inspector);
	if(inspector instanceof ToggleSwitch ts)
	    tile.setActionHandler(ts::fire);
	else
	    tile.setActionHandler(inspector::requestFocus);
	inspectorPane.getChildren().add(tile);
    }

    private void addButton(String labelName, String description, Runnable action) {
	var tile = new Tile(labelName, description);
	tile.setActionHandler(action);
	inspectorPane.getChildren().add(tile);
    }
}


