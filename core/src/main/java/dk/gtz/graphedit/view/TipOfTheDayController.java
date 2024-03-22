package dk.gtz.graphedit.view;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.Tile;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.util.InspectorUtils;
import dk.gtz.graphedit.viewmodel.Tip;
import dk.gtz.graphedit.viewmodel.TipContainer;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TipOfTheDayController {
    @FXML
    private BorderPane root;
    @FXML
    private VBox inspectorPane;
    private ViewModelEditorSettings editorSettings;
    private Tip tip;
    private final TipContainer tips;
    private int currentTipIndex;

    public TipOfTheDayController() {
	this.editorSettings = DI.get(ViewModelEditorSettings.class);
	this.tips = DI.get(TipContainer.class);
	currentTipIndex = editorSettings.tipIndex().get();
    }

    @FXML
    private void initialize() {
	tip = tips.get(currentTipIndex);
	inspectorPane.getChildren().clear();
	inspectorPane.getChildren().add(new Separator());
	var title = new Label(tip.category());
	title.getStyleClass().add(Styles.TITLE_3);
	title.getStyleClass().add(Styles.CENTER);
	inspectorPane.getChildren().add(title);
	inspectorPane.getChildren().add(new Text(tip.description()));
	inspectorPane.getChildren().add(new Separator());
	root.setTop(getTopPane());
	root.setBottom(getBottomPane("Show", "Show tip of the day on startup", editorSettings.showTips()));
	editorSettings.tipIndex().set(currentTipIndex+1 % tips.size());
	EditorActions.saveEditorSettings(editorSettings);
    }

    private Node getTopPane() {
	var pane = new BorderPane();
	var prevBtn = new Button("Previous");
	prevBtn.setGraphic(new FontIcon(BootstrapIcons.ARROW_LEFT));
	prevBtn.setOnAction(e -> {
	    currentTipIndex--;
	    if(currentTipIndex < 0)
		currentTipIndex = tips.size()-1;
	    initialize();
	});
	pane.setLeft(prevBtn);

	var nextBtn = new Button("Next");
	nextBtn.setGraphic(new FontIcon(BootstrapIcons.ARROW_RIGHT));
	nextBtn.setOnAction(e -> {
	    currentTipIndex++;
	    currentTipIndex %= tips.size();
	    initialize();
	});
	pane.setRight(nextBtn);

	pane.setCenter(new Label("%d/%d".formatted((currentTipIndex % tips.size())+1, tips.size())));
	return pane;
    }

    private Node getBottomPane(String labelName, String description, Observable observable) {
	var inspector = InspectorUtils.getObservableInspector(observable);
	var tile = new Tile(labelName, description);
	tile.setAction(inspector);
	if(inspector instanceof ToggleSwitch ts)
	    tile.setActionHandler(() -> {
		ts.fire();
		EditorActions.saveEditorSettings(editorSettings);
	    });
	else
	    tile.setActionHandler(() -> {
		inspector.requestFocus();
		EditorActions.saveEditorSettings(editorSettings);
	    });
	return tile;
    }
}
