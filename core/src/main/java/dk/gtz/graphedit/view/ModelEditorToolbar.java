package dk.gtz.graphedit.view;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.viewmodel.SyntaxFactoryCollection;
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
    private Logger logger = LoggerFactory.getLogger(ModelEditorToolbar.class);
    private final IToolbox toolbox;
    private final ObjectProperty<ITool> selectedTool;
    private final ViewModelProjectResource resource;

    public ModelEditorToolbar(IToolbox toolbox, ObjectProperty<ITool> selectedTool, ViewModelProjectResource resource) {
	this.toolbox = toolbox;
	this.selectedTool = selectedTool;
	this.resource = resource;
	setupStyle();
    }

    public ModelEditorToolbar withSyntaxSelector() {
	if(resource.metadata().containsKey("graphedit_syntax"))
	    addSyntaxSelector();
	addSeparator();
	return this;
    }

    public ModelEditorToolbar withButtons() {
	setupContent();
	return this;
    }

    private void setupStyle() {
	setOrientation(Orientation.HORIZONTAL);
    }

    private void setupContent() {
	for(var toolCategory : toolbox.getToolsByCategory().entrySet()) {
	    for(var tool : toolCategory.getValue())
		addButton(tool);
	    addSeparator();
	}
    }

    private void addSyntaxSelector() {
	// TODO: This should be an EditorAction (The VetoChangeListener should still exist)
	var factories = DI.get(SyntaxFactoryCollection.class);
	ObservableList<String> list = FXCollections.observableArrayList();
	for(var factory : factories.entrySet())
	    list.add(factory.getKey());
	var cmb = new ComboBox<>(list);
	cmb.getSelectionModel().select(resource.metadata().get("graphedit_syntax"));
	getItems().add(cmb);
	var listener = new VetoChangeListener<String>(cmb.getSelectionModel()) {
	    @Override
	    protected boolean isInvalidChange(String oldValue, String newValue) {
		if(!resource.syntax().isEmpty()) {
		    logger.warn("Can't change syntax on a non-empty graph. Create a new one or delete all syntactic elements in this one");
		    return true;
		}
		return false;
	    }

	    @Override
	    protected void onChanged(String oldValue, String newValue) {
		resource.metadata().put("graphedit_syntax", newValue);
	    }
	};
	cmb.getSelectionModel().selectedItemProperty().addListener(listener);
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

