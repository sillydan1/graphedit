package dk.gtz.graphedit.view;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.tool.IToolbox;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow.AnchorLocation;

public class ModelEditorToolbar extends ToolBar {
    private final IToolbox toolbox;
    private final ObjectProperty<ITool> selectedTool;

    public ModelEditorToolbar(IToolbox toolbox, ObjectProperty<ITool> selectedTool) {
	this.toolbox = toolbox;
	this.selectedTool = selectedTool;
	setupStyle();
	setupContent();
    }

    private void setupStyle() {
	setOrientation(Orientation.VERTICAL);
    }

    private void setupContent() {
	for(var toolCategory : toolbox.getToolsByCategory().entrySet()) {
	    for(var tool : toolCategory.getValue())
		addButton(tool);
	    addSeparator();
	}
    }

    private void addButton(ITool tool) {
	var btn = new ToggleButton(null, tool.getGraphic());
	btn.getStyleClass().addAll(Styles.BUTTON_ICON);
	getItems().add(btn);
	btn.setOnMouseClicked(e -> selectedTool.set(tool));
	selectedTool.addListener((e,o,n) -> btn.selectedProperty().set(n == tool));
	if(tool.getTooltip().isEmpty())
	    return;
	var tip = new Tooltip(tool.getTooltip().get());
	tip.setAnchorLocation(AnchorLocation.WINDOW_TOP_RIGHT);
	tip.setPrefWidth(200);
	tip.setWrapText(true);
	btn.setTooltip(tip);
    }

    private void addSeparator() {
	getItems().add(new Separator(Orientation.HORIZONTAL));
    }
}

