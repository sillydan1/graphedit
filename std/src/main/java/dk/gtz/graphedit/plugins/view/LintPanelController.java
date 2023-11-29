package dk.gtz.graphedit.plugins.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.LintContainer;
import dk.gtz.graphedit.viewmodel.ViewModelLint;
import dk.yalibs.yadi.DI;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LintPanelController extends StackPane {
    private final VBox container;
    private final ScrollPane scrollPane;
    private final LintContainer lints;
    private final Map<ViewModelLint,Node> lintNodeMapping;

    public LintPanelController() {
	lintNodeMapping = new HashMap<>();
	container = new VBox();
	container.setSpacing(10);
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
	    var lintContainer = new VBox();
	    lintContainer.setSpacing(5);
	    lintContainer.getChildren().add(bufferKeyTitle);
	    addDebuggingButton(lintContainer, lintCollection.getKey());
	    for(var lint : lintCollection.getValue())
		addLint(lintContainer, lint);
	    lintCollection.getValue().addListener((ListChangeListener<ViewModelLint>)c -> {
		c.next();
		for(var lint : c.getAddedSubList())
		    addLint(lintContainer, lint);
		for(var lint : c.getRemoved()) {
		    var lintNode = lintNodeMapping.get(lint);
		    if(lintNode == null)
			continue;
		    lintContainer.getChildren().remove(lintNode);
		    lintNodeMapping.remove(lint);
		}
	    });
	    container.getChildren().addAll(lintContainer, new Separator());
	}
    }

    private void addLint(VBox container, ViewModelLint lint) {
	// TODO: Consider using Notification styles instead? - That way, the style is uniform
	var card = new Card();
	card.getStyleClass().add(Styles.INTERACTIVE);
	var icon = IconUtils.getLintTypeIcon(lint.severity().get());
	switch(lint.severity().get()) {
	    case ERROR: icon.getStyleClass().add(Styles.DANGER); break;
	    case WARNING: icon.getStyleClass().add(Styles.WARNING); break;
	    case INFO:
	    default: break;
	}
	var title = new HBox(icon, new Label(lint.title().get()));
	title.setSpacing(5);
	title.getStyleClass().add(Styles.TITLE_4);
	card.setHeader(title);
	var body = new TextFlow(new Text(lint.message().get()));
	body.maxWidthProperty().bind(container.prefWidthProperty());
	card.setBody(body);
	card.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> lint.focus());
	lintNodeMapping.put(lint, card);
	container.getChildren().add(card);
    }

    private ModelLintSeverity randomSeverity() {
	var pick = new Random().nextInt(ModelLintSeverity.values().length);
	return ModelLintSeverity.values()[pick];
    }

    private void addDebuggingButton(VBox container, String bufferKey) {
	var button = new Button("Add random lint");
	button.setOnAction(e -> {
	    try {
		var buffer = DI.get(IBufferContainer.class).get(bufferKey);
		var r = new Random();
		var randomSelection = new ArrayList<UUID>();
		for(var key : buffer.syntax().vertices().keySet())
		    if(r.nextBoolean())
			randomSelection.add(key);

		var rr = r.nextInt(999);
		var randomLint = new ViewModelLint(new ModelLint(
			    bufferKey,
			    "E%d".formatted(rr),
			    randomSeverity(),
			    "lint title (E%d)".formatted(rr),
			    """
			    Description goes here
			    """,
			    Optional.empty(),
			    randomSelection,
			    List.of()));
		lints.add(bufferKey, randomLint);
	    } catch(Exception exc) {
		throw new RuntimeException(exc);
	    }
	});
	container.getChildren().add(button);
    }

    private void initializeEventHandlers() {
	lints.getProperty().addListener((e,o,n) -> setLints());
    }
}
