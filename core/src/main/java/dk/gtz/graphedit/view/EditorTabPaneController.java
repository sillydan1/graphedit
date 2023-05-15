package dk.gtz.graphedit.view;

import dk.gtz.graphedit.skyhook.DI;
import javafx.beans.binding.Bindings;
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
	DI.add(this.getClass(), this);
	initPlaceholderText();
    }

    private void initPlaceholderText() {
        var bb = Bindings.isEmpty(tabpane.getTabs());
        placeholder.visibleProperty().bind( bb );
        placeholder.managedProperty().bind( bb );
        root.alignmentProperty().bind( Bindings.when( bb ).then( Pos.CENTER ).otherwise( Pos.TOP_LEFT ) );
    }
}

