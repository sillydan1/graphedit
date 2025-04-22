package dk.gtz.graphedit.view;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.viewmodel.IFocusable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View controller for search result display.
 */
public class SearchResultController extends VBox {
	private IFocusable focusable;

	/**
	 * Construct a new instance
	 * 
	 * @param icon      The icon indicating the type of search result
	 * @param text      The text to display on the search result
	 * @param focusable The focusable object that was found during the search
	 */
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

	/**
	 * Focus on the search result object
	 */
	public void focus() {
		focusable.focus();
	}
}
