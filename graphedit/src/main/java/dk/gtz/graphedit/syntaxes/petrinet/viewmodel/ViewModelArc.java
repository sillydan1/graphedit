package dk.gtz.graphedit.syntaxes.petrinet.viewmodel;

import java.util.List;

import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.syntaxes.petrinet.model.ModelArc;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ViewModelArc extends ViewModelEdge implements ISearchable {
    private final IntegerProperty weight;

    public ViewModelArc(ModelEdge edge) {
        super(edge);
        weight = new SimpleIntegerProperty(1);
        if(edge instanceof ModelArc tedge)
            weight.set(tedge.weight);
    }

    public IntegerProperty weight() {
        return weight;
    }

    @Override
    public List<String> getSearchValues() {
        return List.of();
    }
}

