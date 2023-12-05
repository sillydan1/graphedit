package dk.gtz.graphedit.tool;

import java.util.stream.Collectors;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelDiff;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelSelection;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MassDeleteTool extends AbstractBaseTool {
    private final ObservableList<ViewModelSelection> selectedElements;
    private final IUndoSystem undoSystem;
    private final IBufferContainer buffers;

    public MassDeleteTool() {
	selectedElements = DI.get("selectedElements");
	undoSystem = DI.get(IUndoSystem.class);
        buffers = DI.get(IBufferContainer.class);
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.TRASH);
    }

    @Override
    public String getHelpDescription() {
	return """
	    """;
    }

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {
	if(isDeleteKeyCombo(e.event()))
	    deleteSelectedElements(e);
    }

    private boolean isDeleteKeyCombo(KeyEvent event) {
        var delete = event.getCode().equals(KeyCode.DELETE);
        // This is good for keyboards without a delete button (e.g. some macbooks)
        var shortcutShiftBackspace = (event. getCode().equals(KeyCode.BACK_SPACE)) && event.isShortcutDown() && event.isShiftDown();
        return delete || shortcutShiftBackspace;
    }

    public void deleteSelectedElements(ViewportKeyEvent event) {
	try {
	    var buffer = buffers.get(event.bufferId());
	    var prev = new ViewModelProjectResource(buffer.toModel(), event.syntax());
	    for(var elem : selectedElements) {
		var linkedEdges = buffer.syntax().edges()
		    .entrySet()
		    .stream()
		    .filter(e -> e.getValue().source().get().equals(elem.id()) || e.getValue().target().get().equals(elem.id()))
		    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())); 
		for(var edge : linkedEdges.entrySet()) 
		    buffer.syntax().edges().remove(edge.getKey());
		buffer.syntax().vertices().remove(elem.id());
		buffer.syntax().edges().remove(elem.id());
	    }
	    var diff = ViewModelDiff.compare(prev, buffer);
	    undoSystem.push(new Undoable("mass delete",
			() -> ViewModelDiff.revert(buffer, diff),
			() -> ViewModelDiff.apply(buffer, diff)));
	} catch(Exception exc) {
	    throw new RuntimeException(exc);
	}
    }
}
