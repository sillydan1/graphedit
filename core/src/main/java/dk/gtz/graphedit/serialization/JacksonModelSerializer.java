package dk.gtz.graphedit.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.ModelProjectResource;

public class JacksonModelSerializer implements IModelSerializer {
    private final ObjectMapper objectMapper;

    public JacksonModelSerializer() {
	this.objectMapper = getMapper();
    }

    public ObjectMapper getMapper() {
	var om = new ObjectMapper();
	om.registerModule(new Jdk8Module());
	var ptv = BasicPolymorphicTypeValidator.builder()
	    // allow collection types
	    .allowIfSubType("java.util.HashMap")
	    .allowIfSubType("java.util.ArrayList")
	    // allow the basics
	    .allowIfSubType("com.baeldung.jackson.inheritance")
	    .allowIfSubType("java.awt")
	    // allow graphedit 
	    .allowIfSubType("dk.gtz.graphedit")
	    .build();
	om.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
	return om;
    }

    @Override
    public String serialize(ModelProjectResource model) throws SerializationException {
	try {
	    return objectMapper.writeValueAsString(model);
	} catch (JsonProcessingException e) {
	    throw new SerializationException(e);
	}
    }

    @Override
    public ModelProjectResource deserialize(String serializedContent) throws SerializationException {
	try {
	    return objectMapper.readValue(serializedContent, ModelProjectResource.class);
	} catch (JsonProcessingException e) {
	    throw new SerializationException(e);
	}
    }
}

