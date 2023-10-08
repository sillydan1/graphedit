package dk.gtz.graphedit.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionModel;

public abstract class VetoChangeListener<T> implements ChangeListener<T> {
    private final SelectionModel<T> selectionModel;
    private boolean changing = false;

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

    protected abstract boolean isInvalidChange(T oldValue, T newValue);

    protected abstract void onChanged(T oldValue, T newValue);
}

