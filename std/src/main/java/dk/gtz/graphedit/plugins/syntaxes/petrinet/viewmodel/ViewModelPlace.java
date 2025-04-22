package dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelPlace;
import dk.gtz.graphedit.viewmodel.Autolisten;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ViewModelPlace extends ViewModelVertex implements ISearchable {
	@Autolisten
	public final IntegerProperty initialTokenCount;

	public IntegerProperty initialTokenCount() {
		return initialTokenCount;
	}

	public ViewModelPlace(UUID uuid, ModelVertex vertex) {
		super(uuid, vertex, new ViewModelVertexShape(ViewModelShapeType.OVAL));
		initialTokenCount = new SimpleIntegerProperty(0);
		if (vertex instanceof ModelPlace tvertex)
			initialTokenCount.set(tvertex.initialTokenCount());
	}

	public ViewModelPlace(UUID uuid, ViewModelVertex vertex) {
		this(uuid, vertex.toModel());
	}

	@Override
	public List<String> getSearchValues() {
		return List.of("tokens: %d".formatted(initialTokenCount.get()));
	}

	@Override
	public ModelPlace toModel() {
		return new ModelPlace(position().getValue().toModel(), initialTokenCount.get());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return A list of inspectable objects
	 */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var inspectables = new ArrayList<>(super.getInspectableObjects());
		inspectables.add(new InspectableProperty("Initial Token Count", initialTokenCount));
		return inspectables;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		if (!(other instanceof ViewModelPlace vother))
			return false;
		return initialTokenCount.get() == vother.initialTokenCount.get();
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ initialTokenCount.getValue().hashCode();
	}
}
