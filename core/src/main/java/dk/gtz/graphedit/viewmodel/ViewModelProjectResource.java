package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.gtz.graphedit.model.ModelProjectResource;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

public class ViewModelProjectResource implements IFocusable {
    private SimpleMapProperty<String,String> metadata;
    private ViewModelGraph syntax;
    private List<Runnable> onFocusHandlers;

    public ViewModelProjectResource(ModelProjectResource projectResource) {
	this(new SimpleMapProperty<>(FXCollections.observableHashMap()), new ViewModelGraph(projectResource.syntax()));
	metadata.putAll(projectResource.metadata());
    }

    public ViewModelProjectResource(SimpleMapProperty<String,String> metadata, ViewModelGraph syntax) {
	this.metadata = metadata;
	this.syntax = syntax;
	this.onFocusHandlers = new ArrayList<>();
    }

    public ModelProjectResource toModel() {
	return new ModelProjectResource(new HashMap<>(metadata.get()), syntax.toModel());
    }

    @Override
    public void addFocusListener(Runnable focusEventHandler) {
	onFocusHandlers.add(focusEventHandler);
    }

    @Override
    public void focus() {
	onFocusHandlers.forEach(Runnable::run);
    }

    public SimpleMapProperty<String, String> metadata() {
	return metadata;
    }

    public ViewModelGraph syntax() {
	return syntax;
    }
}

