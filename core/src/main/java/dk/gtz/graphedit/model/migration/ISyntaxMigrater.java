package dk.gtz.graphedit.model.migration;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@FunctionalInterface
public interface ISyntaxMigrater {
    TreeNode migrate(TreeNode input, ObjectMapper objectMapper); // TODO: Try to genericify it with TreeNodes instead
}

