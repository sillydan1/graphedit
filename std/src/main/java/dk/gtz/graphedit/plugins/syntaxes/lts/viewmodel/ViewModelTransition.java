package dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.plugins.syntaxes.lts.model.ModelTransition;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ViewModelTransition extends ViewModelEdge implements ISearchable {
	private final Logger logger = LoggerFactory.getLogger(ViewModelTransition.class);
	private StringProperty action;

	public StringProperty action() {
		return action;
	}

	public ViewModelTransition(ModelEdge edge) {
		super(edge);
		this.action = new SimpleStringProperty("tau");
		if(edge instanceof ModelTransition tedge)
			this.action.set(tedge.action());
	}

	public ViewModelTransition(ModelTransition edge) {
		super(edge);
		logger.info(edge.action());
		this.action = new SimpleStringProperty(edge.action());
	}

	public ViewModelTransition(ViewModelEdge edge) {
		super(edge.source(), edge.target());
		this.action = new SimpleStringProperty("tau");
	}

	/**
	 * {@inheritDoc}
	 * @return A list of inspectable objects
	 * */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var inspectables = new ArrayList<>(super.getInspectableObjects());
		inspectables.add(new InspectableProperty("Action", action));
		return inspectables;
	}

	@Override
	public List<String> getSearchValues() {
		return List.of(action.getValueSafe());
	}

	@Override
	public ModelEdge toModel() {
		return new ModelTransition(source().get(), target().get(), action.getValueSafe());
	}

	@Override
	public void addListener(ChangeListener<? super ViewModelEdge> listener) {
		super.addListener(listener);
		action.addListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void removeListener(ChangeListener<? super ViewModelEdge> listener) {
		super.addListener(listener);
		action.removeListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void addListener(InvalidationListener listener) {
		super.addListener(listener);
		action.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		super.addListener(listener);
		action.removeListener(listener);
	}

	@Override
	public void setValue(ViewModelEdge value) {
		super.setValue(value);
		if(value instanceof ViewModelTransition tvalue)
			action.setValue(tvalue.action().get());
	}

	@Override
	public void bind(ObservableValue<? extends ViewModelEdge> observable) {
		super.bind(observable);
		if(observable.getValue() instanceof ViewModelTransition tobs)
			action.bind(tobs.action());
	}

	@Override
	public void unbind() {
		super.unbind();
		action.unbind();
	}

	@Override
	public boolean isBound() {
		return super.isBound() && action.isBound();
	}

	@Override
	public void bindBidirectional(Property<ViewModelEdge> other) {
		super.bindBidirectional(other);
		if(other.getValue() instanceof ViewModelTransition tother)
			action.bindBidirectional(tother.action());
	}

	@Override
	public void unbindBidirectional(Property<ViewModelEdge> other) {
		super.unbindBidirectional(other);
		if(other.getValue() instanceof ViewModelTransition tother)
			action.unbindBidirectional(tother.action());
	}
}

