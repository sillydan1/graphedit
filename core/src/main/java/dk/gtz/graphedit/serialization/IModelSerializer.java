package dk.gtz.graphedit.serialization;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.ModelEditorSettings;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.model.ModelProjectResource;

public interface IModelSerializer {
    String serialize(ModelProject model) throws SerializationException;
    String serialize(ModelProjectResource model) throws SerializationException;
    String serializeEditorSettings(ModelEditorSettings settings) throws SerializationException;

    ModelProjectResource deserialize(String serializedContent) throws SerializationException;
    ModelProjectResource deserialize(File file) throws SerializationException, IOException;
    ModelProject deserializeProject(String serializedContent) throws SerializationException;
    ModelProject deserializeProject(File file) throws SerializationException, IOException;
    ModelEditorSettings deserializeEditorSettings(String serializedContent) throws SerializationException;
    ModelEditorSettings deserializeEditorSettings(File file) throws SerializationException, IOException;

    List<String> getSupportedContentTypes();

    void addClassLoader(ClassLoader loader);
}

