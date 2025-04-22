package dk.gtz.graphedit.tool;

import java.util.Optional;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import dk.gtz.graphedit.events.EdgeMouseEvent;
import dk.gtz.graphedit.events.VertexMouseEvent;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.events.ViewportMouseEvent;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.gtz.graphedit.viewmodel.ViewModelSelection;
import dk.yalibs.yadi.DI;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Tool to select syntactic elements.
 *
 * When selected, click either a vertex or an edge to mark it as selected.
 * You can select more than one element at a time by holding down CTRL whilst
 * clicking on a syntactic element.
 */
public class SelectTool extends AbstractBaseTool {
	private final ObservableList<ViewModelSelection> selectedElements;

	/**
	 * Construct a new instance
	 */
	public SelectTool() {
		selectedElements = DI.get("selectedElements");
	}

	@Override
	public String getHelpDescription() {
		return """
				Tool to select syntactic elements.

				When selected, click either a vertex or an edge to mark it as selected.
				You can select more than one element at a time by holding down <CTRL> whilst clicking on a syntactic element.
				""";
	}

	@Override
	public Optional<String> getTooltip() {
		return Optional.of("select syntactic elements");
	}

	@Override
	public Node getGraphic() {
		return new FontIcon(BootstrapIcons.CURSOR);
	}

	@Override
	public void onKeyEvent(ViewportKeyEvent e) {
		if (e.event().getEventType().equals(KeyEvent.KEY_RELEASED))
			if (e.event().getCode().equals(KeyCode.ESCAPE))
				clear();
	}

	@Override
	public void onViewportMouseEvent(ViewportMouseEvent e) {
		if (!e.isTargetDrawPane())
			return;
		if (e.event().getEventType().equals(MouseEvent.MOUSE_CLICKED))
			if (!e.event().isControlDown())
				clear();
	}

	@Override
	public void onVertexMouseEvent(VertexMouseEvent e) {
		if (e.event().getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
			if (!e.event().isControlDown())
				clear();
			toggleSelected(e.vertexId(), e.vertex());
		}
	}

	@Override
	public void onEdgeMouseEvent(EdgeMouseEvent e) {
		if (e.event().getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
			if (!e.event().isControlDown())
				clear();
			toggleSelected(e.edgeId(), e.edge());
		}
	}

	/**
	 * Clear the selection
	 */
	public void clear() {
		for (var e : selectedElements)
			e.selectable().deselect();
		selectedElements.clear();
	}

	/**
	 * Toggle the selection of a syntactic element
	 * 
	 * @param id         The id of the element to select / deselect
	 * @param selectable The selectable object to select / deselect
	 */
	public void toggleSelected(UUID id, ISelectable selectable) {
		if (selectedElements.stream().anyMatch(e -> e.id().equals(id) && e.selectable() == selectable)) {
			selectedElements.removeIf(e -> e.id().equals(id) && e.selectable() == selectable);
			selectable.deselect();
		} else {
			selectedElements.add(new ViewModelSelection(id, selectable));
			selectable.select();
		}
	}

	/**
	 * Get the current selection
	 * 
	 * @return An observable list of selections
	 */
	public ObservableList<ViewModelSelection> getSelection() {
		return selectedElements;
	}
}
