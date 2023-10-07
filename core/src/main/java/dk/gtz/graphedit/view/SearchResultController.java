package dk.gtz.graphedit.view;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.viewmodel.IFocusable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SearchResultController extends VBox {
    private IFocusable focusable;

    public SearchResultController(BootstrapIcons icon, String text, IFocusable focusable) {
	var ficon = new FontIcon(icon);
	var label = new Label(text);
	label.setAlignment(Pos.CENTER_LEFT);
	this.focusable = focusable;
	var box = new HBox(ficon, label);
	box.setSpacing(10);
	getChildren().setAll(box);
	box.setAlignment(Pos.CENTER_LEFT);
	VBox.setVgrow(box, Priority.ALWAYS);
    }

    public void focus() {
	focusable.focus();
    }
}

