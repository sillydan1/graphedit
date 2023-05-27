package dk.gtz.graphedit.serialization;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.ModelProjectResource;

public interface IModelSerializer {
    public String serialize(ModelProjectResource model) throws SerializationException;

    public ModelProjectResource deserialize(String serializedContent) throws SerializationException;
}

