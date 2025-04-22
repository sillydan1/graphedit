package dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel;

import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelTransition;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;

public class ViewModelTransition extends ViewModelVertex implements ISearchable {
	public ViewModelTransition(UUID uuid, ModelVertex vertex) {
		super(uuid, vertex);
	}

	@Override
	public List<String> getSearchValues() {
		return List.of();
	}

	@Override
	public ModelTransition toModel() {
		return new ModelTransition(position().getValue().toModel());
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		if (!(other instanceof ViewModelTransition))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
