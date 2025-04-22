package dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelArc;
import dk.gtz.graphedit.viewmodel.Autolisten;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ViewModelArc extends ViewModelEdge implements ISearchable {
	private static final Logger logger = LoggerFactory.getLogger(ViewModelArc.class);
	@Autolisten
	public final IntegerProperty weight;

	public ViewModelArc(UUID uuid, ModelEdge edge) {
		super(uuid, edge);
		weight = new SimpleIntegerProperty(1);
		if (edge instanceof ModelArc tedge)
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
		if (source instanceof ViewModelPlace) {
			var targetIsTransition = targetCandidate instanceof ViewModelTransition;
			if (!targetIsTransition)
				logger.warn("arcs from places are only allowed to target transitions");
			return targetIsTransition;
		}
		if (source instanceof ViewModelTransition) {
			var targetIsPlace = targetCandidate instanceof ViewModelPlace;
			if (!targetIsPlace)
				logger.warn("arcs from transitions are only allowed to target places");
			return targetIsPlace;
		}
		logger.error("unrecognized source vertex type: %s".formatted(source.getClass().getName()));
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return A list of inspectable objects
	 */
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
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		if (!(other instanceof ViewModelArc vother))
			return false;
		return weight.get() == vother.weight.get();
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ weight.getValue().hashCode();
	}
}
