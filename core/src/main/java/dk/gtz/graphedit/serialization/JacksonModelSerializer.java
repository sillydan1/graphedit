package dk.gtz.graphedit.serialization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelEditorSettings;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.util.MetadataUtils;

/**
 * Implementation of {@link IModelSerializer} using the jackson xml serializer
 */
public class JacksonModelSerializer implements IModelSerializer {
	private final Logger logger = LoggerFactory.getLogger(JacksonModelSerializer.class);
	private ObjectMapper objectMapper;
	private List<String> pluginPackages;

	/**
	 * Construct a new instance
	 */
	public JacksonModelSerializer() {
		pluginPackages = new ArrayList<>();
		this.objectMapper = getMapper();
	}

	private ObjectMapper getMapper() {
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
				.allowIfSubType("dk.gtz.graphedit");
		for (var p : pluginPackages)
			ptv.allowIfSubType(p);
		om.activateDefaultTyping(ptv.build(), ObjectMapper.DefaultTyping.NON_FINAL);
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
		return om;
	}

	@Override
	public String getPreferedFileExtension() {
		return ".json";
	}

	@Override
	public void addClassLoader(ClassLoader loader) {
		for (var p : loader.getDefinedPackages()) {
			logger.trace("adding '{}' to allowed namespaces", p.getName());
			pluginPackages.add(p.getName());
		}
		var tf = this.objectMapper.getTypeFactory().withClassLoader(loader);
		this.objectMapper = getMapper();
		this.objectMapper.setTypeFactory(tf);
	}

	@Override
	public String serialize(ModelVertex vertex) throws SerializationException {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vertex);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public String serialize(ModelEdge edge) throws SerializationException {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(edge);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public String serialize(ModelProject model) throws SerializationException {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public String serialize(ModelProjectResource model) throws SerializationException {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public ModelProjectResource deserializeProjectResource(String serializedContent) throws SerializationException {
		try {
			TreeNode node = objectMapper.readTree(serializedContent);
			var factory = MetadataUtils.getSyntaxFactory(node.get("metadata").get(1));
			var migrater = factory.getMigrater();
			if (migrater.isPresent())
				node = migrater.get().migrate(node, objectMapper);
			else
				logger.trace("no migrater available, trying to raw-dog it");
			return objectMapper.treeToValue(node, ModelProjectResource.class);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public ModelProjectResource deserializeProjectResource(File file) throws SerializationException, IOException {
		try {
			TreeNode node = objectMapper.readTree(file);
			var factory = MetadataUtils.getSyntaxFactory(node.get("metadata").get(1));
			var migrater = factory.getMigrater();
			if (migrater.isPresent())
				node = migrater.get().migrate(node, objectMapper);
			else
				logger.trace("no migrater available, trying to raw-dog it");
			return objectMapper.treeToValue(node, ModelProjectResource.class);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public ModelProject deserializeProject(String serializedContent) throws SerializationException {
		try {
			return objectMapper.readValue(serializedContent, ModelProject.class);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public ModelProject deserializeProject(File file) throws SerializationException, IOException {
		try {
			return objectMapper.readValue(file, ModelProject.class);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public List<String> getSupportedContentTypes() {
		return List.of("text/plain", "application/json");
	}

	@Override
	public String serialize(ModelEditorSettings settings) throws SerializationException {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public ModelEditorSettings deserializeEditorSettings(String serializedContent) throws SerializationException {
		try {
			return objectMapper.readValue(serializedContent, ModelEditorSettings.class);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public ModelEditorSettings deserializeEditorSettings(File file) throws SerializationException, IOException {
		try {
			return objectMapper.readValue(file, ModelEditorSettings.class);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e);
		}
	}
}
