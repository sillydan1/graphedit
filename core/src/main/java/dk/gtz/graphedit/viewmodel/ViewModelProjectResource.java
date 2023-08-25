package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.gtz.graphedit.model.ModelProjectResource;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;

public class ViewModelProjectResource implements IFocusable {
    private SimpleMapProperty<String,String> metadata;
    private ViewModelGraph syntax;
    private List<Runnable> onFocusHandlers;
    private List<Tab> views;

    public ViewModelProjectResource(ModelProjectResource projectResource) {
	this(new SimpleMapProperty<>(FXCollections.observableHashMap()), new ViewModelGraph(projectResource.syntax()));
	metadata.putAll(projectResource.metadata());
    }

    public ViewModelProjectResource(SimpleMapProperty<String,String> metadata, ViewModelGraph syntax) {
	this.metadata = metadata;
	this.syntax = syntax;
	this.onFocusHandlers = new ArrayList<>();
	this.views = new ArrayList<>();
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

    public void addView(Tab viewer) {
	views.add(viewer);
    }

    public void removeView(Tab viewer) {
	views.remove(viewer);
    }

    public List<Tab> getViews() {
	return views;
    }
}

