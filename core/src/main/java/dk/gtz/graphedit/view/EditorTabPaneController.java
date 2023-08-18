package dk.gtz.graphedit.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(EditorTabPaneController.class);
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
	// TODO: This behavior makes it impossible to drag detached tabs into the main window when empty
        root.alignmentProperty().bind(Bindings.when(bb).then(Pos.CENTER).otherwise(Pos.TOP_LEFT));
    }
    
    private void initTabpaneBufferContainer() {
	DI.get(IBufferContainer.class).getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
	    var changedKey = c.getKey();
	    if(c.wasAdded()) {
		var changedVal = c.getValueAdded();
		var tab = new DraggableTab(changedKey);
		tab.setOnClosed(e -> DI.get(IBufferContainer.class).close(changedKey)); // TODO: Reconsider closing the buffer here. It creates some weirdness in the undo-tree. Maybe it would be better to separate the concepts View/Window and Buffer, just like vim?
		var editorController = new ModelEditorController(changedVal, DI.get(ISyntaxFactory.class));
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
		tabpane.getTabs().removeIf(t -> t.getText().equals(changedKey));
	});
    }
}

