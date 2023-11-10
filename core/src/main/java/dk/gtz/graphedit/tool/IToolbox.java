package dk.gtz.graphedit.tool;

import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;

/**
 * Interface for container of {@link ITool}s
 * Can be used as a builder-pattern.
 */
public interface IToolbox {
    /**
     * Get all tools in the toolbox by category
     * @return A mapping of category names to lists of tools
     */
    Map<String,List<ITool>> getToolsByCategory();

    /**
     * Add the tool that will be selected by default
     * @param tool The tool to be selected by default
     * @return A builder-pattern style reference to this
     */
    IToolbox addDefaultTool(ITool tool);

    /**
     * Add a list of tools to the toolbox
     * @param tool Varargs of tools to add
     * @return A builder-pattern style reference to this
     */
    IToolbox add(ITool... tool);

    /**
     * Add a list of tools to the toolbox and put them in a category
     * @param category The category of the provided tools
     * @param tool Varargs of tools to add
     * @return A builder-pattern style reference to this
     */
    IToolbox add(String category, ITool... tool);

    /**
     * Get the default tool
     * @return A tool instance
     */
    ITool getDefaultTool();

    /**
     * Change which tool is currently selected
     * @param tool The tool to select
     */
    void selectTool(ITool tool);

    /**
     * Get the currently selected tool observable
     * @return An observable pointing to the currenlty selected tool
     */
    ObjectProperty<ITool> getSelectedTool();
}
