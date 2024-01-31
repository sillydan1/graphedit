package dk.gtz.graphedit.serialization;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelEditorSettings;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;

/**
 * Interface for model serializer implementations
 */
public interface IModelSerializer {
    /**
     * Serialize a model vertex to a string value
     * @param vertex The model vertex to serialize
     * @return A serialized string representing the provided vertex
     * @throws SerializationException if something went wrong during serialization
     */
    String serialize(ModelVertex vertex) throws SerializationException;

    /**
     * Serialize a model edge to a string value
     * @param edge The model edge to serialize
     * @return A serialized string representing the provided edge
     * @throws SerializationException if something went wrong during serialization
     */
    String serialize(ModelEdge edge) throws SerializationException;

    /**
     * Serialize a model project object to a string value
     * @param model The model project to serialize
     * @return A serialized string representing the provided model
     * @throws SerializationException if something went wrong during serialization
     */
    String serialize(ModelProject model) throws SerializationException;

    /**
     * Serialize a model project resource object to a string value
     * @param model The model project resource to serialize
     * @return A serialized string representing the provided model
     * @throws SerializationException if something went wrong during serialization
     */
    String serialize(ModelProjectResource model) throws SerializationException;

    /**
     * Serialize a model editor settings object to a string value
     * @param model The model editor settings to serialize
     * @return A serialized string representing the provided model
     * @throws SerializationException if something went wrong during serialization
     */
    String serialize(ModelEditorSettings model) throws SerializationException;

    /**
     * Desertialize a model project resource object from a string value
     * @param serializedContent A string of content to deserialize
     * @return A new {@link ModelProjectResource} instance based on the serialized content
     * @throws SerializationException if something went wrong during deserialization
     */
    ModelProjectResource deserializeProjectResource(String serializedContent) throws SerializationException;

    /**
     * Desertialize a model project resource object from a file
     * @param file A file handle containing serialized content
     * @return A new {@link ModelProjectResource} instance based on the serialized content of the provided file
     * @throws SerializationException if something went wrong during deserialization
     * @throws IOException if reading the file failed
     */
    ModelProjectResource deserializeProjectResource(File file) throws SerializationException, IOException;

    /**
     * Desertialize a model project object from a string value
     * @param serializedContent A string of content to deserialize
     * @return A new {@link ModelProject} instance based on the serialized content
     * @throws SerializationException if something went wrong during deserialization
     */
    ModelProject deserializeProject(String serializedContent) throws SerializationException;

    /**
     * Desertialize a model project object from a file
     * @param file A file handle containing serialized content
     * @return A new {@link ModelProject} instance based on the serialized content of the provided file
     * @throws SerializationException if something went wrong during deserialization
     * @throws IOException if reading the file failed
     */
    ModelProject deserializeProject(File file) throws SerializationException, IOException;

    /**
     * Desertialize a model editor settings object from a string value
     * @param serializedContent A string of content to deserialize
     * @return A new {@link ModelEditorSettings} instance based on the serialized content
     * @throws SerializationException if something went wrong during deserialization
     */
    ModelEditorSettings deserializeEditorSettings(String serializedContent) throws SerializationException;

    /**
     * Desertialize a model editor settings object from a file
     * @param file A file handle containing serialized content
     * @return A new {@link ModelEditorSettings} instance based on the serialized content of the provided file
     * @throws SerializationException if something went wrong during deserialization
     * @throws IOException if reading the file failed
     */
    ModelEditorSettings deserializeEditorSettings(File file) throws SerializationException, IOException;

    /**
     * Get a list of supported content types.
     * These are typically in the MIME content format
     * @return A list of types that this serializer supports
     */
    List<String> getSupportedContentTypes();

    /**
     * Add another classloader to the serializer.
     * This is useful when creating plugins with non-core model objects
     * @param loader The additional loader to use when resolving classes
     */
    void addClassLoader(ClassLoader loader);

    /**
     * Get the preferred file extension for this serializer. e.g. ".json"
     * @return A string representing the preferred file extension
     */
    String getPreferedFileExtension();
}
