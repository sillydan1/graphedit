package dk.gtz.graphedit.view;

import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
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
    private VBox editor;

    @FXML
    private void initialize() {
	initPlaceholderText();
	initTabpaneBufferContainer();
    }

    private void initPlaceholderText() {
        var bb = Bindings.isEmpty(tabpane.getTabs());
        placeholder.visibleProperty().bind( bb );
        placeholder.managedProperty().bind( bb );
	// TODO: This behavior makes it impossible to drag detached tabs into the main window when empty... I will fix this later
        root.alignmentProperty().bind( Bindings.when( bb ).then( Pos.CENTER ).otherwise( Pos.TOP_LEFT ) );
    }

    private void loadModelEditor() throws Exception {
    }
    
    private void initTabpaneBufferContainer() {
	DI.get(IBufferContainer.class).getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
	    var changedKey = c.getKey();
	    if(c.wasAdded()) {
		var changedVal = c.getValueAdded();
		var tab = new DraggableTab(changedKey);
		var editorController = new ModelEditorController(changedVal); // TODO: This should be loaded as an fxml file instead
		tab.setContent(editorController);
		tabpane.getTabs().add(tab);
	    }
	    if(c.wasRemoved())
		tabpane.getTabs().removeIf(t -> t.getText().equals(changedKey));
	});
    }
}

