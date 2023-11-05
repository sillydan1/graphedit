package dk.gtz.graphedit.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dk.gtz.graphedit.model.migration.ISyntaxMigrater;
import dk.gtz.graphedit.view.util.MetadataUtils;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelVertex;

public class DemoSyntaxMigrater implements ISyntaxMigrater {
    private final Logger logger = LoggerFactory.getLogger(DemoSyntaxMigrater.class);

    private String getMigraterVersion() {
        return "v1.0.0";
    }

    @Override
    public TreeNode migrate(TreeNode input, ObjectMapper objectMapper) {
        var metadata = (ObjectNode)input.get("metadata").get(1);
        var factory = MetadataUtils.getSyntaxFactory(metadata);
        var syntax = metadata.get("graphedit_syntax");
        if(syntax == null || !syntax.asText().equals(factory.getSyntaxName()))
            metadata.put("graphedit_syntax", factory.getSyntaxName());

        var migraterVersion = metadata.get("graphedit_latest_migrater_version");
        if(migraterVersion != null && migraterVersion.asText().equals(getMigraterVersion()))
            return input;

        logger.trace("Migrating vertices");
        var vertices = (ObjectNode)input.get("syntax").get("vertices").get(1);
        var vertexChanges = objectMapper.createObjectNode();
        var vertexIterator = vertices.fieldNames();
        while(vertexIterator.hasNext()) {
            var vertexKey = vertexIterator.next();
            var vertex = vertices.get(vertexKey);
            if(vertex.isObject()) {
                var newArray = objectMapper.createArrayNode();
                newArray.add(ModelVertex.class.getName());
                newArray.add(vertex);
                vertexChanges.set(vertexKey, newArray);
            }
        }
        if(!vertexChanges.isEmpty()) {
            logger.trace("Migrating {} vertices", vertexChanges.size());
            ((ArrayNode)input.get("syntax").get("vertices")).remove(1);
            ((ArrayNode)input.get("syntax").get("vertices")).add(vertexChanges);
        }

        logger.trace("Migrating edges");
        var edges = (ObjectNode)input.get("syntax").get("edges").get(1);
        var edgesChanges = objectMapper.createObjectNode();
        var edgeIterator = edges.fieldNames();
        while(edgeIterator.hasNext()) {
            var edgeKey = edgeIterator.next();
            var edge = edges.get(edgeKey);
            if(edge.isObject()) {
                var newArray = objectMapper.createArrayNode();
                newArray.add(ModelEdge.class.getName());
                newArray.add(edge);
                edgesChanges.set(edgeKey, newArray);
            }
        }
        if(!edgesChanges.isEmpty()) {
            logger.trace("Migrating {} edges", edgesChanges.size());
            ((ArrayNode)input.get("syntax").get("edges")).remove(1);
            ((ArrayNode)input.get("syntax").get("edges")).add(edgesChanges);
        }
        metadata.put("graphedit_latest_migrater_version", getMigraterVersion());
        return input;
    }
}

