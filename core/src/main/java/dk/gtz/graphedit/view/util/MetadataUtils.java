package dk.gtz.graphedit.view.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import dk.gtz.graphedit.view.DemoSyntaxFactory;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.yalibs.yadi.DI;

public class MetadataUtils {
    private static Logger logger = LoggerFactory.getLogger(MetadataUtils.class);

    public static ISyntaxFactory getSyntaxFactory(Map<String,String> metadata) {
	return getSyntaxFactory(metadata, new DemoSyntaxFactory());
    }

    public static ISyntaxFactory getSyntaxFactory(TreeNode metadataNode) {
	return getSyntaxFactory(metadataNode, new DemoSyntaxFactory());
    }

    public static ISyntaxFactory getSyntaxFactory(String syntax) {
	return getSyntaxFactory(syntax, new DemoSyntaxFactory());
    }

    public static ISyntaxFactory getSyntaxFactory(Map<String,String> metadata, ISyntaxFactory defaultValue) {
	// TODO: magic values
	metadata.putIfAbsent("graphedit_syntax", defaultValue.getSyntaxName());
	return getSyntaxFactory(metadata.get("graphedit_syntax"), defaultValue);
    }

    public static ISyntaxFactory getSyntaxFactory(TreeNode metadataNode, ISyntaxFactory defaultValue) {
	if(metadataNode.path("graphedit_syntax").isMissingNode())
	    return defaultValue;
	var val = (ValueNode)metadataNode.get("graphedit_syntax");
	return getSyntaxFactory(val.asText(), defaultValue);
    }

    public static ISyntaxFactory getSyntaxFactory(String syntax, ISyntaxFactory defaultValue) {
	// TODO: syntax_factories should be a class / type of itself
	var factories = (Map<String,ISyntaxFactory>)DI.get("syntax_factories");
	if(factories.containsKey(syntax))
	    return factories.get(syntax);
	for(var factory : factories.entrySet())
	    if(factory.getValue().getLegacyNames().stream().filter(s -> s.equals(syntax)).findAny().isPresent())
		return factory.getValue();
	logger.warn("No such syntax: {}", syntax);
	return defaultValue;
    }
}

