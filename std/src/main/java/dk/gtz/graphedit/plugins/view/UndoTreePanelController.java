package dk.gtz.graphedit.plugins.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import dk.gtz.graphedit.util.IObservableUndoSystem;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UndoTreePanelController extends StackPane {
    private final VBox container;
    private final ListView<String> list;
    private ChangeListener<Undoable> undoListener;

    public UndoTreePanelController() {
        list = new ListView<>();
	container = new VBox(list);
	list.getStyleClass().add(Styles.DENSE);
	list.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
	list.getStyleClass().add("text-monospace");
	getChildren().add(container);

        var bufferContainer = DI.get(IBufferContainer.class);
        bufferContainer.getCurrentlyFocusedBuffer().addListener((e,o,n) -> {
            if(o != null) {
                o.getUndoSystem().getCurrentUndoableProperty().removeListener(undoListener);
                o.getUndoSystem().removeListener(undoListener);
            }
            setList(n);
        });
        setList(bufferContainer.getCurrentlyFocusedBuffer().get());
    }

    private void setList(ViewModelProjectResource currentBuffer) {
        if(currentBuffer == null)
            return;
        setList(currentBuffer.getUndoSystem());
        undoListener = (e,o,n) -> setList(currentBuffer.getUndoSystem());
        currentBuffer.getUndoSystem().getCurrentUndoableProperty().addListener(undoListener);
        currentBuffer.getUndoSystem().addListener(undoListener);
    }

    private void setList(IObservableUndoSystem undosystem) {
        var history = getUndoHistory(undosystem);
	list.getItems().setAll(history);
    }

    private List<String> getUndoHistory(IObservableUndoSystem undosystem) {
        var result = new ArrayList<String>();
        var current = undosystem.getCurrentAction();
        for(var undoable : undosystem.getHistory()) {
            if(current.isPresent() && undoable == current.get())
                result.add(" * [" + undoable.getDescription() + "]");
            else
                result.add(" * " + undoable.getDescription());
        }
        if(result.isEmpty())
            result.add("<empty>");
        Collections.reverse(result);
        return result;
    }
}
