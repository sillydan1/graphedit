package dk.gtz.graphedit.spi;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface for syntax migraters.
 * This is useful for syntax plugins whenever they change the model format in a breaking manner.
 * Migraters can then migrate from an old version to a newer one.
 */
@FunctionalInterface
public interface ISyntaxMigrater {
    // TODO: This is 100% dependent on Jackson serialization... It's technically the best serializer for java, but it's still a hard coupling, which is bad.
    TreeNode migrate(TreeNode input, ObjectMapper objectMapper);
}

