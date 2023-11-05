package dk.gtz.graphedit.view;

import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.util.MetadataUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yadi.DI;
import javafx.beans.binding.Bindings;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EditorTabPaneController {
    @FXML
    public TabPane tabpane;
    @FXML
    public Text placeholder;
    @FXML
    public VBox root;

    @FXML
    private void initialize() {
	initPlaceholderText();
	initTabpaneBufferContainer();
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
		var tab = new DraggableTab(tabTitle); // TODO: title should be the ViewModelProject.name stringproperty instead
		changedVal.addView(tab);
		changedVal.addListener((e,o,n) -> tab.setHighlight());
		EditorActions.addSaveListener(tab::unsetHighlight);
		tab.setOnClosed(e ->  {
		    changedVal.removeView(tab);
		    if(changedVal.getViews().isEmpty())
			DI.get(IBufferContainer.class).close(changedKey); 
		});
		var editorController = new ModelEditorController(changedVal, MetadataUtils.getSyntaxFactory(changedVal.metadata()));
		tab.setContent(editorController);
		tabpane.getTabs().add(tab);
		editorController.addFocusListener(() -> {
		    tabpane.getSelectionModel().select(tab);
		    tabpane.requestFocus();
		});
		c.getValueAdded().addFocusListener(() -> {
		    tabpane.getSelectionModel().select(tab);
		    tabpane.requestFocus();
		});
	    }
	    if(c.wasRemoved())
		c.getValueRemoved().getViews().forEach(v -> tabpane.getTabs().remove(v));
	});
    }
}

