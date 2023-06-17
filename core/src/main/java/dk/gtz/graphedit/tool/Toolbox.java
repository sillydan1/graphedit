package dk.gtz.graphedit.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Toolbox implements IToolbox {
    private static Logger logger = LoggerFactory.getLogger(Toolbox.class);
    private final String defaultCategory;
    private final Map<String, List<ITool>> tools;
    private ITool defaultTool;

    public Toolbox(String defaultCategory, ITool defaultTool) {
        this.defaultCategory = defaultCategory;
        this.defaultTool = defaultTool;
        tools = new HashMap<>();
        add(getDefaultTool());
    }

    @Override
    public Map<String, List<ITool>> getToolsByCategory() {
        return tools;
    }

    @Override
    public IToolbox addDefaultTool(ITool tool) {
        defaultTool = tool;
        return add(tool);
    }

    @Override
    public IToolbox add(ITool... tool) {
        return add(defaultCategory, tool);
    }

    @Override
    public IToolbox add(String category, ITool... tool) {
        if(!tools.containsKey(category))
            tools.put(category, new ArrayList<>());
        var catTools = tools.get(category);
        for(var t : tool) {
            logger.trace("adding '{}' to toolbox category '{}'", t.getClass().getSimpleName(), category);
            catTools.add(t);
        }
        return this;
    }

    @Override
    public ITool getDefaultTool() {
        return defaultTool;
    }
}

