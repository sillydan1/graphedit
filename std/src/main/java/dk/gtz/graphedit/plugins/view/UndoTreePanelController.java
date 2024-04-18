package dk.gtz.graphedit.plugins.view;

import java.util.Collections;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import dk.gtz.graphedit.util.IObservableUndoSystem;
import dk.gtz.graphedit.util.ObservableUndoable;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.Undoable;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class UndoTreePanelController extends VBox {
    private ListView<ObservableUndoable> list;
    private ChangeListener<Undoable> undoListener;

    public UndoTreePanelController() {
        var bufferContainer = DI.get(IBufferContainer.class);
        initialize(bufferContainer);
        bufferContainer.getCurrentlyFocusedBuffer().addListener((e,o,n) -> {
            if(o != null) {
                o.getUndoSystem().getCurrentUndoableProperty().removeListener(undoListener);
                o.getUndoSystem().removeListener(undoListener);
            }
            setList(n);
        });
    }

    private void initialize(IBufferContainer bufferContainer) {
        list = new ListView<>();
        list.getStyleClass().add(Styles.DENSE);
        list.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        list.getStyleClass().add("text-monospace");
        list.setFixedCellSize(Font.font("monospace").getSize() + 4);
        VBox.setVgrow(list, Priority.ALWAYS);
        getChildren().add(list);
        setList(bufferContainer.getCurrentlyFocusedBuffer().get());
        list.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            var index = list.getSelectionModel().getSelectedItem();
            if(index == null)
                return;
            var buffer = DI.get(IBufferContainer.class).getCurrentlyFocusedBuffer().get();
            if(buffer == null)
                return;
            buffer.getUndoSystem().gotoAction(index.undoable());
        });
    }

    private void setList(ViewModelProjectResource currentBuffer) {
        if(currentBuffer == null) {
            list.getItems().clear();
            return;
        }
        setList(currentBuffer.getUndoSystem());
        undoListener = (e,o,n) -> setList(currentBuffer.getUndoSystem());
        currentBuffer.getUndoSystem().getCurrentUndoableProperty().addListener(undoListener);
        currentBuffer.getUndoSystem().addListener(undoListener);
    }

    private void setList(IObservableUndoSystem undosystem) {
        var history = undosystem.getStringRepresentation();
        Collections.reverse(history);
        list.getItems().setAll(history);
    }
}
