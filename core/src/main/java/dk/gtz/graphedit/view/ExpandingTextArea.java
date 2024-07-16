package dk.gtz.graphedit.view;

import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * A text area that expands to fit its content automatically.
 */
public class ExpandingTextArea extends TextArea {
    private Text text;
    private double topOffset;
    private double bottomOffset;

    /**
     * Construct a new expanding text area.
     */
    public ExpandingTextArea() {
	this("");
    }

    /**
     * Construct a new expanding text area with the given text.
     * @param text the text to display in the text area
     */
    public ExpandingTextArea(String text) {
        super(text);
        getStyleClass().add("expanding-text-area");
        setWrapText(true);
        sceneProperty().addListener(it -> {
            if(getScene() != null)
                performBinding();
        });
        skinProperty().addListener(it -> {
            if(getSkin() != null)
                performBinding();
        });
    }

    private double computeHeight() {
        computeOffsets();
        var layoutBounds = localToScreen(text.getLayoutBounds());
	if(layoutBounds == null)
	    return 0;
	double minY = layoutBounds.getMinY();
	double maxY = layoutBounds.getMaxY();
	return maxY - minY + topOffset + bottomOffset;
    }

    private void computeOffsets() {
        topOffset = getInsets().getTop();
        bottomOffset = getInsets().getBottom();
        var scrollPane = (ScrollPane)lookup(".scroll-pane");
	if(scrollPane == null)
	    return;
	var viewport = (Region)scrollPane.lookup(".viewport");
	var content = (Region)scrollPane.lookup(".content");
	topOffset += viewport.getInsets().getTop();
	topOffset += content.getInsets().getTop();
	bottomOffset += viewport.getInsets().getBottom();
	bottomOffset += content.getInsets().getBottom();
    }

    private void performBinding() {
        ScrollPane scrollPane = (ScrollPane) lookup(".scroll-pane");
	if(scrollPane == null)
	    return;
	scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	scrollPane.skinProperty().addListener(it -> {
	    if(scrollPane.getSkin() == null)
		return;
	    if(text != null)
		return;
	    var newText = findTextNode();
	    if(newText.isPresent()) {
		text = newText.get();
		prefHeightProperty().bind(Bindings.createDoubleBinding(this::computeHeight, text.layoutBoundsProperty()));
	    }
	});
    }

    private Optional<Text> findTextNode() {
        var nodes = lookupAll(".text");
        for(var node : nodes)
            if(node.getParent() instanceof Group)
                return Optional.of((Text)node);
        return Optional.empty();
    }
}
