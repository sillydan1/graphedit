package dk.gtz.graphedit.serialization;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.model.ModelProjectResource;

public interface IModelSerializer {
    public String serialize(ModelProjectResource model) throws SerializationException;

    public ModelProjectResource deserialize(String serializedContent) throws SerializationException;
    public ModelProjectResource deserialize(File file) throws SerializationException, IOException;
    public ModelProject deserializeProject(String serializedContent) throws SerializationException;
    public ModelProject deserializeProject(File file) throws SerializationException, IOException;

    public List<String> getSupportedContentTypes();
}

