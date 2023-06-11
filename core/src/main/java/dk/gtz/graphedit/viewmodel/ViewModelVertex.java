package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelVertex;

// TODO: Should also keep some information about the shape (width, height, shape (oval,rectangle)) for presentation purposes
public record ViewModelVertex(ViewModelPoint position) {
    public ViewModelVertex(ModelVertex vertex) {
        this(new ViewModelPoint(vertex.position()));
    }
}

