package dk.gtz.graphedit.spi;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface for syntax migraters.
 * This is useful for syntax plugins whenever they change the model format in a
 * breaking manner.
 * Migraters can then migrate from an old version to a newer one.
 */
@FunctionalInterface
public interface ISyntaxMigrater {
	/**
	 * Perform a migration on a serialized tree node structure using the Jackson
	 * serialization library
	 *
	 * @param input        The parsed tree node structure to migrate
	 * @param objectMapper The associated jackson objectmapper
	 * @return A new parse-tree that has been patched
	 */
	TreeNode migrate(TreeNode input, ObjectMapper objectMapper);
}
