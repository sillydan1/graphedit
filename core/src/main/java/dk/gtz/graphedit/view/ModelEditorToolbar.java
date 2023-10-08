package dk.gtz.graphedit.view;

import java.util.Map;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yadi.DI;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow.AnchorLocation;

public class ModelEditorToolbar extends ToolBar {
    private final IToolbox toolbox;
    private final ObjectProperty<ITool> selectedTool;
    private final ViewModelProjectResource resource;

    public ModelEditorToolbar(IToolbox toolbox, ObjectProperty<ITool> selectedTool, ViewModelProjectResource resource) {
	this.toolbox = toolbox;
	this.selectedTool = selectedTool;
	this.resource = resource;
	setupStyle();
	setupContent();
    }

    private void setupStyle() {
	setOrientation(Orientation.HORIZONTAL);
    }

    private void setupContent() {
	// Zoom / viewport stuff
	if(resource.metadata().containsKey("graphedit_syntax"))
	    addSyntaxSelector();
	addSeparator();
	for(var toolCategory : toolbox.getToolsByCategory().entrySet()) {
	    for(var tool : toolCategory.getValue())
		addButton(tool);
	    addSeparator();
	}
    }

    private void addSyntaxSelector() {
	var factories = (Map<String,ISyntaxFactory>)DI.get("syntax_factories");
	ObservableList<String> list = FXCollections.observableArrayList();
	for(var factory : factories.entrySet())
	    list.add(factory.getKey());
	var cmb = new ComboBox<>(list);
	cmb.getSelectionModel().select(resource.metadata().get("graphedit_syntax"));
	getItems().add(cmb);
	cmb.getSelectionModel().selectedItemProperty().addListener((e,o,n) -> resource.metadata().put("graphedit_syntax", n));
    }

    private void addButton(ITool tool) {
	var btn = new ToggleButton(null, tool.getGraphic());
	btn.getStyleClass().addAll(Styles.BUTTON_ICON);
	getItems().add(btn);
	btn.setOnMouseClicked(e -> selectedTool.set(tool));
	btn.selectedProperty().set(selectedTool.get() == tool);
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
	getItems().add(new Separator(Orientation.VERTICAL));
    }
}

