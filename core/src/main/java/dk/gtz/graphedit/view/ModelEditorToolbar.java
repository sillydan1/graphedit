package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.tool.ITool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.util.VetoChangeListener;
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

/**
 * Toolbar view controller governing a {@link IToolbox} instance
 */
public class ModelEditorToolbar extends ToolBar {
	private Logger logger = LoggerFactory.getLogger(ModelEditorToolbar.class);
	private final IToolbox toolbox;
	private final ObjectProperty<ITool> selectedTool;
	private final ViewModelProjectResource resource;
	private ComboBox<String> syntaxSelector;

	/**
	 * Create a new instance
	 * 
	 * @param toolbox      The toolbox to govern
	 * @param selectedTool Object property of the currently selected tool
	 * @param resource     The model resource edited by the model editor
	 */
	public ModelEditorToolbar(IToolbox toolbox, ObjectProperty<ITool> selectedTool,
			ViewModelProjectResource resource) {
		this.toolbox = toolbox;
		this.selectedTool = selectedTool;
		this.resource = resource;
		setupStyle();
	}

	/**
	 * Add a syntax selector drop-down menu to the toolbar
	 * 
	 * @return Builder-pattern style reference to this
	 */
	public ModelEditorToolbar withSyntaxSelector() {
		if (resource.metadata().containsKey("graphedit_syntax")) {
			var factories = DI.get(SyntaxFactoryCollection.class);
			syntaxSelector = addSyntaxSelector(factories);
			factories.addChangeListener(e -> {
				getItems().remove(syntaxSelector);
				syntaxSelector = addSyntaxSelector(factories);
			});
		}

		addSeparator();
		return this;
	}

	/**
	 * Add buttons for the tools in the toolbox
	 * 
	 * @return Builder-pattern style reference to this
	 */
	public ModelEditorToolbar withButtons() {
		setupContent();
		return this;
	}

	private void setupStyle() {
		setOrientation(Orientation.HORIZONTAL);
	}

	private void setupContent() {
		for (var toolCategory : toolbox.getToolsByCategory().entrySet()) {
			for (var tool : toolCategory.getValue())
				addButton(tool);
			addSeparator();
		}
	}

	private ComboBox<String> addSyntaxSelector(SyntaxFactoryCollection factories) {
		ObservableList<String> list = FXCollections.observableArrayList();
		for (var factory : factories.entrySet())
			list.add(factory.getKey());
		var cmb = new ComboBox<>(list);
		cmb.getSelectionModel().select(resource.metadata().get("graphedit_syntax"));
		getItems().add(cmb);
		var listener = new VetoChangeListener<String>(cmb.getSelectionModel()) {
			@Override
			protected boolean isInvalidChange(String oldValue, String newValue) {
				if (!resource.syntax().isEmpty()) {
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
		return cmb;
	}

	private void addButton(ITool tool) {
		var btn = new ToggleButton(null, tool.getGraphic());
		btn.getStyleClass().addAll(Styles.BUTTON_ICON);
		getItems().add(btn);
		btn.setOnMouseClicked(e -> selectedTool.set(tool));
		btn.selectedProperty().set(selectedTool.get() == tool);
		selectedTool.addListener((e, o, n) -> btn.selectedProperty().set(n == tool));
		if (tool.getTooltip().isEmpty())
			return;
		var tip = new Tooltip(tool.getTooltip().get());
		tip.setPrefWidth(200);
		tip.setWrapText(true);
		btn.setTooltip(tip);
	}

	private void addSeparator() {
		getItems().add(new Separator(Orientation.VERTICAL));
	}
}
