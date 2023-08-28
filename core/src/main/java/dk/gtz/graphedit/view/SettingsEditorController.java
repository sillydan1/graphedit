package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.view.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsEditorController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsEditorController.class);
    @FXML
    private BorderPane root;
    @FXML
    private VBox inspectorPane;
    private ViewModelEditorSettings editorSettings;

    public void initialize() {
	initRoot();
	editorSettings = DI.get(ViewModelEditorSettings.class);
	addInspector("gridsizeX", editorSettings.gridSizeX());
	addInspector("gridsizeY", editorSettings.gridSizeY());
	addInspector("gridsnap", editorSettings.gridSnap());
	addInspector("useLightTheme", editorSettings.useLightTheme());
	addInspector("autoOpenLastProject", editorSettings.autoOpenLastProject());
	addSaveButton();
    }

    private void initRoot() {
	root.getStyleClass().add(Styles.BG_DEFAULT);
    }

    private void addInspector(String labelName, Observable observable) {
	inspectorPane.getChildren().add(new HBox(new Label(labelName), InspectorUtils.getObservableInspector(observable)));
    }

    private void addSaveButton() {
	var saveButton = new Button("Save Changes");
	saveButton.setOnAction((e) -> EditorActions.saveEditorSettings(editorSettings));
	inspectorPane.getChildren().add(saveButton);
    }
}

