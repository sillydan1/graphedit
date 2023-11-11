package dk.gtz.graphedit.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Implementation of a toolbox
 */
public class Toolbox implements IToolbox {
    private static Logger logger = LoggerFactory.getLogger(Toolbox.class);
    private final String defaultCategory;
    private final Map<String, List<ITool>> tools;
    private ITool defaultTool;
    private SimpleObjectProperty<ITool> selectedTool;

    /**
     * {@link ITool} Constructor wrapper interface, where the constructor takes a parent toolbox argument. Use with: MyTool::new
     */
    @FunctionalInterface
    public static interface IToolConstructor {
        /**
         * Constructor function that takes a parent toolbox
         * @param parent The toolbox that should contain this tool
         * @return A new tool within the parent toolbox
         */
        ITool ctor(IToolbox parent);
    }

    /**
     * Construct a new toolbox with a default category and a constructor for the default tool
     * @param defaultCategory The category of the default tool
     * @param defaultToolCtor Constructor function that can create the default tool. Will be created and selected immediately
     */
    public Toolbox(String defaultCategory, IToolConstructor defaultToolCtor) {
        this.defaultCategory = defaultCategory;
        this.defaultTool = defaultToolCtor.ctor(this);
        this.selectedTool = new SimpleObjectProperty<>();
        this.tools = new HashMap<>();
        add(getDefaultTool());
        this.selectedTool.set(getDefaultTool());
    }

    /**
     * Construct a new toolbox with a default category and a default tool
     * @param defaultCategory The category of the default tool
     * @param defaultTool The default tool. Will be selected immediately
     */
    public Toolbox(String defaultCategory, ITool defaultTool) {
        this.defaultCategory = defaultCategory;
        this.defaultTool = defaultTool;
        this.selectedTool = new SimpleObjectProperty<>();
        this.tools = new HashMap<>();
        add(getDefaultTool());
        this.selectedTool.set(getDefaultTool());
    }

    /**
     * Construct a new toolbox with a default category, a default tool and a list of additional tools
     * @param defaultCategory The category of the default tool
     * @param defaultTool The default tool. Will be selected immediately
     * @param tools List of additional tools to put in the default category
     */
    public Toolbox(String defaultCategory, ITool defaultTool, ITool... tools) {
        this.defaultCategory = defaultCategory;
        this.defaultTool = defaultTool;
        this.selectedTool = new SimpleObjectProperty<>();
        this.tools = new HashMap<>();
        add(getDefaultTool());
        this.selectedTool.set(getDefaultTool());
        this.add(tools);
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

    @Override
    public ObjectProperty<ITool> getSelectedTool() {
        return selectedTool;
    }

    @Override
    public void selectTool(ITool tool) {
        selectedTool.set(tool);
    }
}
