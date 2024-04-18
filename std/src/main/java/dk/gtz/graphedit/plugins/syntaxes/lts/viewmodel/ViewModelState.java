package dk.gtz.graphedit.plugins.syntaxes.lts.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.lts.model.ModelState;
import dk.gtz.graphedit.viewmodel.Autolisten;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ViewModelState extends ViewModelVertex implements ISearchable {
	@Autolisten
	public final StringProperty name;
	@Autolisten
	public final BooleanProperty initial;

	public StringProperty name() {
		return name;
	}

	public BooleanProperty initial() {
		return initial;
	}

	public ViewModelState(UUID uuid, ModelVertex vertex) {
		super(uuid, vertex);
		name = new SimpleStringProperty("");
		initial = new SimpleBooleanProperty(false);
		if(vertex instanceof ModelState tvertex) {
			this.name.set(tvertex.name());
			this.initial.set(tvertex.isInitial());
		}
	}

	public ViewModelState(UUID uuid, ModelState vertex) {
		super(uuid, vertex);
		name = new SimpleStringProperty(vertex.name());
		initial = new SimpleBooleanProperty(vertex.isInitial());
	}

	public ViewModelState(UUID uuid, ViewModelVertex vertex) {
		super(uuid, vertex.position(), vertex.shape());
		name = new SimpleStringProperty("");
		initial = new SimpleBooleanProperty(false);
	}

	/**
	 * {@inheritDoc}
	 * @return A list of inspectable objects
	 * */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var inspectables = new ArrayList<>(super.getInspectableObjects());
		inspectables.add(new InspectableProperty("Initial", initial));
		inspectables.add(new InspectableProperty("Label", name));
		return inspectables;
	}

	@Override
	public List<String> getSearchValues() {
		var result = new ArrayList<>(List.of(name.getValueSafe()));
		if(initial.get())
			result.add("initial");
		return result;
	}

	@Override
	public ModelVertex toModel() {
		return new ModelState(position().toModel(), name.getValueSafe(), initial.get());
	}

	@Override
	public boolean equals(Object other) {
		if(!super.equals(other))
			return false;
		if(!(other instanceof ViewModelState vother))
			return false;
		return name.get().equals(vother.name.get()) && initial.get() == vother.initial.get();
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ name.getValue().hashCode() ^ initial.getValue().hashCode();
	}
}
