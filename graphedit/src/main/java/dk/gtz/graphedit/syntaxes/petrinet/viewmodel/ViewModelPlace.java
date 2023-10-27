package dk.gtz.graphedit.syntaxes.petrinet.viewmodel;

import java.util.List;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.syntaxes.petrinet.model.ModelPlace;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ViewModelPlace extends ViewModelVertex implements ISearchable {
    private final IntegerProperty initialTokenCount;

    public IntegerProperty initialTokenCount() {
        return initialTokenCount;
    }

    public ViewModelPlace(ModelVertex vertex) {
        super(vertex);
        initialTokenCount = new SimpleIntegerProperty(1);
        if(vertex instanceof ModelPlace tvertex)
            initialTokenCount.set(tvertex.initialTokenCount());
    }

    public ViewModelPlace(ViewModelVertex vertex) {
        super(vertex.toModel());
        initialTokenCount = new SimpleIntegerProperty(1);
        if(vertex instanceof ViewModelPlace tvertex)
            initialTokenCount.set(tvertex.initialTokenCount().get());
    }

    public ViewModelPlace(ViewModelPoint position, ViewModelVertexShape shape) {
        super(position, shape);
        this.initialTokenCount = new SimpleIntegerProperty(1);
    }

    @Override
    public List<String> getSearchValues() {
        return List.of("tokens: %d".formatted(initialTokenCount.get()));
    }
}

