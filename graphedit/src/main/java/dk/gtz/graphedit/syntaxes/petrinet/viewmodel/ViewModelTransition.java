package dk.gtz.graphedit.syntaxes.petrinet.viewmodel;

import java.util.List;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.syntaxes.petrinet.model.ModelTransition;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;

public class ViewModelTransition extends ViewModelVertex implements ISearchable {
    public ViewModelTransition(ModelVertex vertex) {
	super(vertex);
    }

    @Override
    public List<String> getSearchValues() {
	return List.of();
    }

    @Override
    public ModelTransition toModel() {
	return new ModelTransition(position().getValue().toModel());
    }
}

