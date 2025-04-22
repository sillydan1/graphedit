package dk.gtz.graphedit.plugins.syntaxes.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dk.gtz.graphedit.plugins.syntaxes.text.model.ModelTextVertex;
import dk.gtz.graphedit.spi.ISyntaxMigrater;
import dk.gtz.graphedit.util.MetadataUtils;

public class TextSyntaxMigrater implements ISyntaxMigrater {
	private final Logger logger = LoggerFactory.getLogger(TextSyntaxMigrater.class);

	private String getMigraterVersion() {
		return "v1.0.1";
	}

	@Override
	public TreeNode migrate(TreeNode input, ObjectMapper objectMapper) {
		var metadata = (ObjectNode) input.get("metadata").get(1);
		var factory = MetadataUtils.getSyntaxFactory(metadata);
		var syntax = metadata.get("graphedit_syntax");
		if (syntax == null || !syntax.asText().equals(factory.getSyntaxName()))
			metadata.put("graphedit_syntax", factory.getSyntaxName());

		var migraterVersion = metadata.get("graphedit_latest_migrater_version");
		if (migraterVersion != null && migraterVersion.asText().equals(getMigraterVersion()))
			return input;

		logger.trace("Migrating vertices");
		var vertices = (ObjectNode) input.get("syntax").get("vertices").get(1);
		var vertexChanges = objectMapper.createObjectNode();
		var vertexIterator = vertices.fieldNames();
		while (vertexIterator.hasNext()) {
			var vertexKey = vertexIterator.next();
			var vertex = vertices.get(vertexKey);
			if (!vertex.isArray())
				continue;
			var newArray = objectMapper.createArrayNode();
			if (vertex.get(0).asText().contains("ModelTextVertex"))
				newArray.add(ModelTextVertex.class.getName());
			else
				throw new RuntimeException("Unable to migrate LTS syntax, unknown vertex type: "
						+ vertex.get(0).asText());
			newArray.add(vertex.get(1));
			vertexChanges.set(vertexKey, newArray);
		}
		if (!vertexChanges.isEmpty()) {
			logger.trace("Migrating {} vertices", vertexChanges.size());
			((ArrayNode) input.get("syntax").get("vertices")).remove(1);
			((ArrayNode) input.get("syntax").get("vertices")).add(vertexChanges);
		}

		metadata.put("graphedit_latest_migrater_version", getMigraterVersion());
		return input;
	}
}
