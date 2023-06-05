package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelVertex;

public record ViewModelVertex(ViewModelPoint position) {
    public ViewModelVertex(ModelVertex vertex) {
        this(new ViewModelPoint(vertex.position()));
    }
}

