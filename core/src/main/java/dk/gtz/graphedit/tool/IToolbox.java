package dk.gtz.graphedit.tool;

import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;

public interface IToolbox {
    Map<String,List<ITool>> getToolsByCategory();
    IToolbox addDefaultTool(ITool tool);
    IToolbox add(ITool... tool);
    IToolbox add(String category, ITool... tool);
    ITool getDefaultTool();
    void selectTool(ITool tool);
    ObjectProperty<ITool> getSelectedTool();
}

