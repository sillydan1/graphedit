package dk.gtz.graphedit.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import dk.gtz.graphedit.internal.DemoSyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import dk.gtz.graphedit.viewmodel.SyntaxFactoryCollection;
import dk.yalibs.yadi.DI;

/**
 * Static utility class for interacting with metadata
 */
public class MetadataUtils {
	private static Logger logger = LoggerFactory.getLogger(MetadataUtils.class);
	private static String SYNTAX_KEY = "graphedit_syntax";

	private MetadataUtils() {
	}

	/**
	 * Get the syntax factory mentioned in the provided metadata mapping.
	 * Default to {@link DemoSyntaxFactory} if the syntax is not found.
	 * 
	 * @param metadata Metadata mapping
	 * @return An {@link ISyntaxFactory} instance
	 */
	public static ISyntaxFactory getSyntaxFactory(Map<String, String> metadata) {
		return getSyntaxFactory(metadata, new DemoSyntaxFactory());
	}

	/**
	 * Get the syntax factory mentioned in the provided metadata mapping.
	 * Default to {@link DemoSyntaxFactory} if the syntax is not found.
	 * 
	 * @param metadataNode Metadata mapping as an intermediate treenode format
	 * @return An {@link ISyntaxFactory} instance
	 */
	public static ISyntaxFactory getSyntaxFactory(TreeNode metadataNode) {
		return getSyntaxFactory(metadataNode, new DemoSyntaxFactory());
	}

	/**
	 * Get the syntax factory with the provided syntax name
	 * Default to {@link DemoSyntaxFactory} if the syntax is not found.
	 * 
	 * @param syntax Name of the syntax to get
	 * @return An {@link ISyntaxFactory} instance
	 */
	public static ISyntaxFactory getSyntaxFactory(String syntax) {
		return getSyntaxFactory(syntax, new DemoSyntaxFactory());
	}

	/**
	 * Get the syntax factory mentioned in the provided metadata mapping.
	 * 
	 * @param metadata     Metadata mapping
	 * @param defaultValue Returns this if the syntax is not found
	 * @return An {@link ISyntaxFactory} instance
	 */
	public static ISyntaxFactory getSyntaxFactory(Map<String, String> metadata, ISyntaxFactory defaultValue) {
		metadata.putIfAbsent(SYNTAX_KEY, defaultValue.getSyntaxName());
		return getSyntaxFactory(metadata.get(SYNTAX_KEY), defaultValue);
	}

	/**
	 * Get the syntax factory mentioned in the provided metadata mapping.
	 * 
	 * @param metadataNode Metadata mapping as an intermediate treenode format
	 * @param defaultValue Returns this if the syntax is not found
	 * @return An {@link ISyntaxFactory} instance
	 */
	public static ISyntaxFactory getSyntaxFactory(TreeNode metadataNode, ISyntaxFactory defaultValue) {
		if (metadataNode.path(SYNTAX_KEY).isMissingNode())
			return defaultValue;
		var val = (ValueNode) metadataNode.get(SYNTAX_KEY);
		return getSyntaxFactory(val.asText(), defaultValue);
	}

	/**
	 * Get the syntax factory with the provided syntax name
	 * 
	 * @param syntax       Name of the syntax to get
	 * @param defaultValue Returns this if the syntax is not found
	 * @return An {@link ISyntaxFactory} instance
	 */
	public static ISyntaxFactory getSyntaxFactory(String syntax, ISyntaxFactory defaultValue) {
		var factories = DI.get(SyntaxFactoryCollection.class);
		if (factories.containsKey(syntax))
			return factories.get(syntax);
		for (var factory : factories.entrySet())
			if (factory.getValue().getLegacyNames().stream().filter(s -> s.equals(syntax)).findAny()
					.isPresent())
				return factory.getValue();
		logger.warn("no such syntax: {}", syntax);
		return defaultValue;
	}
}
