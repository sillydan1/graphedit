package dk.gtz.graphedit.view;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.viewmodel.IFocusable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SearchResultController extends HBox {
    private IFocusable focusable;

    public SearchResultController(BootstrapIcons icon, String text, IFocusable focusable) {
	getChildren().setAll(new FontIcon(icon), new Label(text));
	this.focusable = focusable;
    }

    public void focus() {
	focusable.focus();
    }
}

