package dk.gtz.graphedit.model.migration;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@FunctionalInterface
public interface ISyntaxMigrater {
    // TODO: This is 100% dependent on Jackson serialization... It's technically the best serializer for java, but it's still a hard coupling, which is bad.
    TreeNode migrate(TreeNode input, ObjectMapper objectMapper);
}

