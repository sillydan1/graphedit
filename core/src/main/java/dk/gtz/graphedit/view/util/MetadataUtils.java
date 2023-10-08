package dk.gtz.graphedit.view.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.view.DemoSyntaxFactory;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.yalibs.yadi.DI;

public class MetadataUtils {
    private static Logger logger = LoggerFactory.getLogger(MetadataUtils.class);

    public static ISyntaxFactory getSyntaxFactory(Map<String,String> metadata) {
	return getSyntaxFactory(metadata, new DemoSyntaxFactory());
    }

    public static ISyntaxFactory getSyntaxFactory(Map<String,String> metadata, ISyntaxFactory defaultValue) {
	// TODO: magic values
	// TODO: syntax_factories should be a class / type of itself
	var factories = (Map<String,ISyntaxFactory>)DI.get("syntax_factories");
	if(metadata.containsKey("graphedit_syntax")) {
	    var syntax = metadata.get("graphedit_syntax");
	    if(factories.containsKey(syntax))
		return factories.get(syntax);
	    else
		logger.warn("No such syntax: {}", syntax);
	}
	metadata.putIfAbsent("graphedit_syntax", defaultValue.getSyntaxName());
	return defaultValue;
    }
}

