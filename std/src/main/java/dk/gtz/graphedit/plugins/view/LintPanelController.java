package dk.gtz.graphedit.plugins.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.LintContainer;
import dk.gtz.graphedit.viewmodel.ViewModelLint;
import dk.yalibs.yadi.DI;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LintPanelController extends StackPane {
    private final VBox container;
    private final ScrollPane scrollPane;
    private final LintContainer lints;

    public LintPanelController() {
	container = new VBox();
	container.setSpacing(5);
	container.setPadding(new Insets(10));
	scrollPane = new ScrollPane(container);
	scrollPane.setFitToWidth(true);
	getChildren().add(scrollPane);
	lints = DI.get(LintContainer.class);
	setLints();
	initializeEventHandlers();
    }

    private void setLints() {
	container.getChildren().clear();
	for(var lintCollection : lints.getProperty().entrySet()) {
	    var bufferKeyTitle = new Label(lintCollection.getKey());
	    bufferKeyTitle.getStyleClass().add(Styles.TITLE_3);
	    bufferKeyTitle.getStyleClass().add(Styles.CENTER);
	    container.getChildren().add(bufferKeyTitle);
	    addDebuggingButton(lintCollection.getKey());
	    // TODO: Consider using Notification styles instead? - That way, the style is uniform
	    for(var lint : lintCollection.getValue()) {
		var card = new Card();
		card.getStyleClass().add(Styles.INTERACTIVE);
		var icon = IconUtils.getLintTypeIcon(lint.severity().get());
		switch(lint.severity().get()) {
		    case ERROR: icon.getStyleClass().add(Styles.DANGER); break;
		    case WARNING: icon.getStyleClass().add(Styles.WARNING); break;
		    case INFO:
		    default: break;
		}
		var title = new HBox(
			icon,
			new Label(lint.title().get()));
		title.setSpacing(5);
		title.getStyleClass().add(Styles.TITLE_4);
		card.setHeader(title);
		var body = new TextFlow(new Text(lint.message().get()));
		body.maxWidthProperty().bind(container.prefWidthProperty());
		card.setBody(body);
		container.getChildren().add(card);
	    }
	    container.getChildren().add(new Separator());
	}
    }

    private ModelLintSeverity randomSeverity() {
	var pick = new Random().nextInt(ModelLintSeverity.values().length);
	return ModelLintSeverity.values()[pick];
    }

    private void addDebuggingButton(String bufferKey) {
	var button = new Button("Add random lint");
	button.setOnAction(e -> {
	    try {
		var buffer = DI.get(IBufferContainer.class).get(bufferKey);
		var r = new Random();
		var randomSelection = new ArrayList<UUID>();
		for(var key : buffer.syntax().vertices().keySet())
		    if(r.nextBoolean())
			randomSelection.add(key);

		var randomLint = new ViewModelLint(new ModelLint(
			    "I001",
			    randomSeverity(),
			    "lint title (E523)",
			    """
			    Description goes here
			    """,
			    randomSelection,
			    List.of()
			    ));
		lints.add(bufferKey, randomLint);
	    } catch(Exception exc) {
		throw new RuntimeException(exc);
	    }
	});
	container.getChildren().add(button);
    }

    private void initializeEventHandlers() {
	lints.getProperty().addListener((e,o,n) -> {
	    setLints();
	});
    }
}
