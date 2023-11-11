package dk.gtz.graphedit.util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionModel;

/**
 * Change listener that can veto a change if it was not a valid change.
 * @param <T> The type of the wrapped value
 */
public abstract class VetoChangeListener<T> implements ChangeListener<T> {
    private final SelectionModel<T> selectionModel;
    private boolean changing = false;

    /**
     * Construct a new instance
     * @param selectionModel The selection to listen for changes in
     */
    public VetoChangeListener(SelectionModel<T> selectionModel) {
        if (selectionModel == null)
            throw new IllegalArgumentException();
        this.selectionModel = selectionModel;
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!changing && isInvalidChange(oldValue, newValue)) {
            changing = true;
            Platform.runLater(() -> {
                selectionModel.select(oldValue);
                changing = false;
            });
        } else
	    onChanged(oldValue, newValue);
    }

    /**
     * Checks if the change was a valid one or not.
     * If the change is invalid, the selection will be reverted
     * @param oldValue The old value
     * @param newValue The new value
     * @return true if the change was considered valid, false otherwise
     */
    protected abstract boolean isInvalidChange(T oldValue, T newValue);

    /**
     * Callback for when the change was valid and accepted.
     * @param oldValue The old value
     * @param newValue The new value
     */
    protected abstract void onChanged(T oldValue, T newValue);
}
