package dk.gtz.graphedit.plugins.syntaxes.petrinet.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.tool.AbstractBaseTool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.events.ViewportKeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class ToolSelectorTool extends AbstractBaseTool {
    private static Logger logger = LoggerFactory.getLogger(ToolSelectorTool.class);
    private final IToolbox parentToolbox;
    private final String category;

    public ToolSelectorTool(IToolbox parent, String category) {
        this.parentToolbox = parent;
        this.category = category;
    }

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {
        if(!e.event().getEventType().equals(KeyEvent.KEY_RELEASED))
            return;
        if(!e.isTargetDrawpane())
            return;
        if(e.event().getCode().equals(KeyCode.DIGIT1))
            safeSelectTool(0);
        if(e.event().getCode().equals(KeyCode.DIGIT2))
            safeSelectTool(1);
        if(e.event().getCode().equals(KeyCode.DIGIT3))
            safeSelectTool(2);
        if(e.event().getCode().equals(KeyCode.DIGIT4))
            safeSelectTool(3);
        if(e.event().getCode().equals(KeyCode.DIGIT5))
            safeSelectTool(4);
        if(e.event().getCode().equals(KeyCode.DIGIT6))
            safeSelectTool(5);
        if(e.event().getCode().equals(KeyCode.DIGIT7))
            safeSelectTool(6);
        if(e.event().getCode().equals(KeyCode.DIGIT8))
            safeSelectTool(7);
        if(e.event().getCode().equals(KeyCode.DIGIT9))
            safeSelectTool(8);
        if(e.event().getCode().equals(KeyCode.DIGIT0))
            safeSelectTool(9);
    }

    private void safeSelectTool(int index) {
        if(index < 0)
            return;
        var tools = parentToolbox.getToolsByCategory().get(category);
        if(index >= tools.size())
            return;
        logger.trace("selecting " + tools.get(index).getClass().getName());
        parentToolbox.selectTool(tools.get(index));
    }
}

