package dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.plugins.syntaxes.lts.model.ModelTransition;
import dk.gtz.graphedit.viewmodel.Autolisten;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ViewModelTransition extends ViewModelEdge implements ISearchable {
	private final Logger logger = LoggerFactory.getLogger(ViewModelTransition.class);
	@Autolisten
	public StringProperty action;

	public StringProperty action() {
		return action;
	}

	public ViewModelTransition(UUID uuid, ModelEdge edge) {
		super(uuid, edge);
		this.action = new SimpleStringProperty("tau");
		if(edge instanceof ModelTransition tedge)
			this.action.set(tedge.action());
	}

	public ViewModelTransition(UUID uuid, ModelTransition edge) {
		super(uuid, edge);
		logger.info(edge.action());
		this.action = new SimpleStringProperty(edge.action());
	}

	public ViewModelTransition(UUID uuid, ViewModelEdge edge) {
		super(uuid, edge.source(), edge.target());
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
	public boolean equals(Object other) {
		if(!super.equals(other))
			return false;
		if(!(other instanceof ViewModelTransition vother))
			return false;
		return action.get().equals(vother.action.get());
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ action.getValue().hashCode();
	}
}
