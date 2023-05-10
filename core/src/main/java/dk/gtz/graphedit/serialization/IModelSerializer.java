package dk.gtz.graphedit.serialization;

import java.io.IOException;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.Model;

public interface IModelSerializer {
    public String serialize(Model model) throws SerializationException;

    public Model deserialize(String serializedContent) throws SerializationException;
}

