package dk.gtz.graphedit.view;

import java.util.Map;

import atlantafx.base.controls.Tile;
import dk.gtz.graphedit.util.Keymap;
import dk.yalibs.yadi.DI;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the keybinds view.
 */
public class KeybindsController {
    @FXML
    private BorderPane root;
    @FXML
    private VBox inspectorPane;
    private Keymap keymap;

    /**
     * Construct a new keybinds controller instance.
     */
    public KeybindsController() {
	this.keymap = DI.get(Keymap.class);
    }

    @FXML
    private void initialize() {
	for(var keybind : keymap.get().entrySet())
	    inspectorPane.getChildren().add(getTile(keybind));
    }

    private Node getTile(Map.Entry<KeyCombination, Keymap.Keybind> keybind) {
	var tile = new Tile(keybind.getValue().description(), keybind.getValue().category());
	tile.setAction(new Label(keybind.getKey().getDisplayText()));
	return tile;
    }
}
