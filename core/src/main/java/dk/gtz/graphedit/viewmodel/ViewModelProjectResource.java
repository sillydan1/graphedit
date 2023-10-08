package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.view.ISyntaxFactory;
import javafx.beans.InvalidationListener;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;

/**
 * View model representation of {@link ModelProjectResource}.
 * Full file-on-disk model. Will include everything a graphedit project file needs
 */
public class ViewModelProjectResource implements IFocusable, Property<ViewModelProjectResource> {
    private MapProperty<String,String> metadata;
    private ViewModelGraph syntax;
    private List<Runnable> onFocusHandlers;
    private List<Tab> views;

    /**
     * Constructs a new view model project resource based on the provided model project resource
     * @param projectResource the model project resource to base on
     */
    public ViewModelProjectResource(ModelProjectResource projectResource, ISyntaxFactory syntaxFactory) {
	this(new SimpleMapProperty<>(FXCollections.observableHashMap()), new ViewModelGraph(projectResource.syntax(), syntaxFactory));
	metadata.putAll(projectResource.metadata());
    }

    /**
     * Constructs a new view model project resource based on provided metadata and view model graph
     * @param metadata a map of metadata, useful for storing properties that aren't necessarily part of the specification
     * @param syntax the graph containing the model
     */
    public ViewModelProjectResource(MapProperty<String,String> metadata, ViewModelGraph syntax) {
	this.metadata = metadata;
	this.syntax = syntax;
	this.onFocusHandlers = new ArrayList<>();
	this.views = new ArrayList<>();
    }

    /**
     * Constructs a new model project resource instance based on the current view model values
     * @return a new model project resource instance
     */
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

    /**
     * Get the metadata map property
     * @return a mapping of strings to strings
     */
    public MapProperty<String, String> metadata() {
	return metadata;
    }

    /**
     * Get the syntax graph
     * @return a view model graph
     */
    public ViewModelGraph syntax() {
	return syntax;
    }

    /**
     * Add a new view that displays this project resource.
     * @param viewer the tab that is currently viewing this project resource
     */
    public void addView(Tab viewer) { // TODO: This shouldn't be a concrete javafx Tab! - it should be an interface
	views.add(viewer);
    }

    /**
     * Remove a view that displays this project resource.
     * If the provided view is not present, the list remains unchanged.
     * @param viewer the tab that is currently viewing this project resource
     */
    public void removeView(Tab viewer) {
	views.remove(viewer);
    }

    /**
     * Get the full list of tabs that are viewing this project resource
     * @return a list of tabs. NOTE: scheduled for a refactor
     */
    public List<Tab> getViews() {
	return views;
    }

    @Override
    public Object getBean() {
	return null;
    }

    @Override
    public String getName() {
	if(metadata.containsKey("name"))
	    return metadata.getValue().get("name");
	return "";
    }

    @Override
    public void addListener(ChangeListener<? super ViewModelProjectResource> listener) {
	metadata.addListener((e,o,n) -> listener.changed(this,this,this));
	syntax.addListener((e,o,n) -> listener.changed(this,this,this));
    }

    @Override
    public void removeListener(ChangeListener<? super ViewModelProjectResource> listener) {
	throw new UnsupportedOperationException("Unimplemented method 'removeListener'");
    }

    @Override
    public ViewModelProjectResource getValue() {
	return this;
    }

    @Override
    public void addListener(InvalidationListener listener) {
	metadata.addListener(listener);
	syntax.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
	metadata.removeListener(listener);
	syntax.removeListener(listener);
    }

    @Override
    public void setValue(ViewModelProjectResource value) {
	metadata.setValue(value.metadata().getValue());
	syntax.setValue(value.syntax().getValue());
    }

    @Override
    public void bind(ObservableValue<? extends ViewModelProjectResource> observable) {
	metadata.bind(observable.getValue().metadata());
	syntax.bind(observable.getValue().syntax());
    }

    @Override
    public void unbind() {
	metadata.unbind();
	syntax.unbind();
    }

    @Override
    public boolean isBound() {
	return metadata.isBound() || syntax.isBound();
    }

    @Override
    public void bindBidirectional(Property<ViewModelProjectResource> other) {
	metadata.bindBidirectional(other.getValue().metadata());
	syntax.bindBidirectional(other.getValue().syntax());
    }

    @Override
    public void unbindBidirectional(Property<ViewModelProjectResource> other) {
	metadata.unbindBidirectional(other.getValue().metadata());
	syntax.unbindBidirectional(other.getValue().syntax());
    }
}

