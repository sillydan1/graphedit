package dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelArc;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ViewModelArc extends ViewModelEdge implements ISearchable {
	private static final Logger logger = LoggerFactory.getLogger(ViewModelArc.class);
	private final IntegerProperty weight;

	public ViewModelArc(UUID uuid, ModelEdge edge) {
		super(uuid, edge);
		weight = new SimpleIntegerProperty(1);
		if(edge instanceof ModelArc tedge)
			weight.set(tedge.weight);
	}

	public IntegerProperty weight() {
		return weight;
	}

	@Override
	public ModelArc toModel() {
		return new ModelArc(source.get(), target.get(), weight.get());
	}

	@Override
	public boolean isTargetValid(UUID target, ViewModelGraph graph) {
		var source = graph.vertices().get(source().get());
		var targetCandidate = graph.vertices().get(target);
		if(source instanceof ViewModelPlace) {
			var targetIsTransition = targetCandidate instanceof ViewModelTransition;
			if(!targetIsTransition)
				logger.warn("arcs from places are only allowed to target transitions");
			return targetIsTransition;
		}
		if(source instanceof ViewModelTransition) {
			var targetIsPlace = targetCandidate instanceof ViewModelPlace;
			if(!targetIsPlace)
				logger.warn("arcs from transitions are only allowed to target places");
			return targetIsPlace;
		}
		logger.error("unrecognized source vertex type: %s".formatted(source.getClass().getName()));
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @return A list of inspectable objects
	 * */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var inspectables = new ArrayList<>(super.getInspectableObjects());
		inspectables.add(new InspectableProperty("Weight", weight));
		return inspectables;
	}

	@Override
	public List<String> getSearchValues() {
		return List.of("weight: " + weight.get());
	}

	@Override
	public void addListener(ChangeListener<? super ViewModelEdge> listener) {
		super.addListener(listener);
		weight.addListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void removeListener(ChangeListener<? super ViewModelEdge> listener) {
		super.addListener(listener);
		weight.removeListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void addListener(InvalidationListener listener) {
		super.addListener(listener);
		weight.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		super.addListener(listener);
		weight.removeListener(listener);
	}

	@Override
	public void setValue(ViewModelEdge value) {
		super.setValue(value);
		if(value instanceof ViewModelArc tvalue)
			weight.setValue(tvalue.weight().get());
	}

	@Override
	public void bind(ObservableValue<? extends ViewModelEdge> observable) {
		super.bind(observable);
		if(observable.getValue() instanceof ViewModelArc tobs)
			weight.bind(tobs.weight());
	}

	@Override
	public void unbind() {
		super.unbind();
		weight.unbind();
	}

	@Override
	public boolean isBound() {
		return super.isBound() && weight.isBound();
	}

	@Override
	public void bindBidirectional(Property<ViewModelEdge> other) {
		super.bindBidirectional(other);
		if(other.getValue() instanceof ViewModelArc tother)
			weight.bindBidirectional(tother.weight());
	}

	@Override
	public void unbindBidirectional(Property<ViewModelEdge> other) {
		super.unbindBidirectional(other);
		if(other.getValue() instanceof ViewModelArc tother)
			weight.unbindBidirectional(tother.weight());
	}

	@Override
	public boolean equals(Object other) {
		if(!super.equals(other))
			return false;
		if(!(other instanceof ViewModelArc vother))
			return false;
		return weight.equals(vother.weight);
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ weight.hashCode();
	}
}
