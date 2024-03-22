package dk.gtz.graphedit.view;

import java.util.HashMap;
import java.util.Map;

import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.util.MetadataUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelDiff;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * View controller for the panel that contains tabs of model editor views as tabs.
 */
public class EditorTabPaneController {
    @FXML
    private TabPane tabpane;
    @FXML
    private Text placeholder;
    @FXML
    private VBox root;
    private Map<Tab,ModelEditorController> tabControllerMapping;

    /**
     * Constructs a new instance of the editor tab panel view controller
     */
    public EditorTabPaneController() {
	tabControllerMapping = new HashMap<>();
    }

    @FXML
    private void initialize() {
	initPlaceholderText();
	initTabpaneBufferContainer();
	initTabEventHandlers();
    }

    private void initPlaceholderText() {
        var bb = Bindings.isEmpty(tabpane.getTabs());
        placeholder.visibleProperty().bind(bb);
        placeholder.managedProperty().bind(bb);
        root.alignmentProperty().bind(Bindings.when(bb).then(Pos.CENTER).otherwise(Pos.TOP_LEFT));
    }
    
    private void initTabpaneBufferContainer() {
	DI.get(IBufferContainer.class).getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
	    var changedKey = c.getKey();
	    if(c.wasAdded()) {
		var changedVal = c.getValueAdded();
		var tabTitle = changedKey;
		if(changedVal.metadata().containsKey("name"))
		    tabTitle = changedVal.metadata().get("name");
		var editorController = new ModelEditorController(changedKey, changedVal, MetadataUtils.getSyntaxFactory(changedVal.metadata()));
		var tab = new DraggableTabController(tabTitle, editorController);
		var lastSavedModel = new SimpleObjectProperty<>(changedVal.toModel());
		var lastSavedModelSyntax = new SimpleObjectProperty<>(MetadataUtils.getSyntaxFactory(changedVal.getSyntaxName().get()));
		changedVal.addView(tab);
		changedVal.addListener((e,o,n) -> {
		    var a = new ViewModelProjectResource(lastSavedModel.get(), lastSavedModelSyntax.get());
		    if(!ViewModelDiff.areComparable(a, n)) {
			tab.setHighlight();
			return;
		    }
		    var diff = ViewModelDiff.compare(a, n);
		    if(diff.isEmpty())
			tab.unsetHighlight();
		    else
			tab.setHighlight();
		});
		EditorActions.addSaveListener(() -> {
		    lastSavedModel.set(changedVal.toModel());
		    lastSavedModelSyntax.set(MetadataUtils.getSyntaxFactory(changedVal.getSyntaxName().get()));
		    tab.unsetHighlight();
		});
		tab.addOnClosedListener(e ->  {
		    changedVal.removeView(tab);
		    if(changedVal.getViews().isEmpty())
			DI.get(IBufferContainer.class).close(changedKey); 
		});
		tab.setOnClosed(e -> {
		    changedVal.removeView(tab);
		    if(changedVal.getViews().isEmpty())
			DI.get(IBufferContainer.class).close(changedKey);
		});
		tab.addEditor(editorController);
		tabpane.getTabs().add(tab);
		tabControllerMapping.put(tab, editorController);
		editorController.addFocusListener(() -> {
		    tabpane.getSelectionModel().select(tab);
		    tabpane.requestFocus();
		});
		c.getValueAdded().addFocusListener(() -> {
		    tabpane.getSelectionModel().select(tab);
		    tabpane.requestFocus();
		});
		c.getValueAdded().focus();
		tab.setOnSelectionChanged(e -> {
		    if(tab.isSelected())
			DI.get(IBufferContainer.class).getCurrentlyFocusedBuffer().set(c.getValueAdded());
		});
	    }
	    if(c.wasRemoved())
		c.getValueRemoved().getViews().forEach(v -> tabpane.getTabs().remove((Object)v));
	});
    }

    private void initTabEventHandlers() {
	Platform.runLater(() -> {
	    tabpane.getScene().addEventHandler(KeyEvent.ANY, e -> {
		var c = tabControllerMapping.get(tabpane.selectionModelProperty().get().getSelectedItem());
		if(c != null)
		    c.onKeyEvent(e);
	    });
	});

	tabpane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
	    c.next();
	    if(c.wasRemoved())
		for(var removedTab : c.getRemoved())
		    tabControllerMapping.remove(removedTab);
	    if(tabpane.getTabs().isEmpty())
		DI.get(IBufferContainer.class).getCurrentlyFocusedBuffer().set(null);
	});
    }
}
