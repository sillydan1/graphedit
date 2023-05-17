package dk.gtz.graphedit.view;

import dk.gtz.graphedit.model.Model;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
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
        placeholder.visibleProperty().bind( bb );
        placeholder.managedProperty().bind( bb );
        root.alignmentProperty().bind( Bindings.when( bb ).then( Pos.CENTER ).otherwise( Pos.TOP_LEFT ) );
    }
    
    private void initTabpaneBufferContainer() {
	DI.get(IBufferContainer.class).getBuffers().addListener((MapChangeListener<String,Model>)c -> {
	    var changedKey = c.getKey();
	    if(c.wasAdded())
		tabpane.getTabs().add(new DraggableTab(changedKey)); // TODO: also have the model
	    if(c.wasRemoved())
		tabpane.getTabs().removeIf(t -> t.getText().equals(changedKey));
	});
    }
}

