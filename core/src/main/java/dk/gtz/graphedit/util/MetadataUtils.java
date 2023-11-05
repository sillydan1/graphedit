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

public class MetadataUtils {
    private static Logger logger = LoggerFactory.getLogger(MetadataUtils.class);
    private static String SYNTAX_KEY = "graphedit_syntax";

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
	metadata.putIfAbsent(SYNTAX_KEY, defaultValue.getSyntaxName());
	return getSyntaxFactory(metadata.get(SYNTAX_KEY), defaultValue);
    }

    public static ISyntaxFactory getSyntaxFactory(TreeNode metadataNode, ISyntaxFactory defaultValue) {
	if(metadataNode.path(SYNTAX_KEY).isMissingNode())
	    return defaultValue;
	var val = (ValueNode)metadataNode.get(SYNTAX_KEY);
	return getSyntaxFactory(val.asText(), defaultValue);
    }

    public static ISyntaxFactory getSyntaxFactory(String syntax, ISyntaxFactory defaultValue) {
	var factories = DI.get(SyntaxFactoryCollection.class);
	if(factories.containsKey(syntax))
	    return factories.get(syntax);
	for(var factory : factories.entrySet())
	    if(factory.getValue().getLegacyNames().stream().filter(s -> s.equals(syntax)).findAny().isPresent())
		return factory.getValue();
	logger.warn("No such syntax: {}", syntax); // TODO: If this happens during file-open, we should rather say "no such syntax found, are you missing a plugin?" or similar
	return defaultValue;
    }
}

