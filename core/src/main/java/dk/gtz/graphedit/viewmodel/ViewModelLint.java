package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class ViewModelLint implements IFocusable {
    private final StringProperty lintIdentifier;
    private final ObjectProperty<ModelLintSeverity> severity;
    private final StringProperty title;
    private final StringProperty message;
    private final ListProperty<UUID> affectedElements;
    private final ListProperty<ListProperty<ViewModelPoint>> affectedRegions;
    private final List<Runnable> focustHandlers;

    public ViewModelLint(ModelLint lint) {
        this.lintIdentifier = new SimpleStringProperty(lint.lintIdentifier());
        this.severity = new SimpleObjectProperty<>(lint.severity());
        this.title = new SimpleStringProperty(lint.title());
        this.message = new SimpleStringProperty(lint.message());
        this.affectedElements = new SimpleListProperty<>(FXCollections.observableList(lint.affectedElements()));
        this.affectedRegions = new SimpleListProperty<>();
        this.focustHandlers = new ArrayList<>();
        for(var region : lint.affectedRegions()) {
            var l = new SimpleListProperty<>(FXCollections.observableList(region.stream().map(ViewModelPoint::new).toList()));
            this.affectedRegions.add(l);
        }
    }

    public StringProperty lintIdentifier() {
        return lintIdentifier;
    }

    public ObjectProperty<ModelLintSeverity> severity() {
        return severity;
    }

    public StringProperty title() {
        return title;
    }

    public StringProperty message() {
        return message;
    }

    public ListProperty<UUID> affectedElements() {
        return affectedElements;
    }

    public ListProperty<ListProperty<ViewModelPoint>> affectedRegions() {
        return affectedRegions;
    }

	@Override
	public void addFocusListener(Runnable focusEventHandler) {
        focustHandlers.add(focusEventHandler);
	}

	@Override
	public void focus() {
        focustHandlers.forEach(Runnable::run);
	}
}
