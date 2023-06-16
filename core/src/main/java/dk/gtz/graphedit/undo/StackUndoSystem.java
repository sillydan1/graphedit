package dk.gtz.graphedit.undo;

import java.util.ArrayList;
import java.util.List;

public class StackUndoSystem implements IUndoSystem {
    private int cursorIndex;
    private final ArrayList<Undoable> actions;

    public StackUndoSystem() {
        this.actions = new ArrayList<>();
        this.cursorIndex = -1;
    }

    @Override
    public void push(Undoable action) {
        if(actions.size()-1 > cursorIndex)
            removeInRange(actions, cursorIndex+1, actions.size()-1);
        actions.add(++cursorIndex, action);
    }

    @Override
    public void undo() {
        if(cursorIndex-1 < -1)
            return;
        actions.get(cursorIndex--).undo();
    }

    @Override
    public void redo() {
        if(cursorIndex+1 > actions.size()-1)
            return;
        actions.get(++cursorIndex).redo();
    }

    // TODO: Move this into the utility library
    public <T> void removeInRange(List<T> list, int startInd, int endInd) {
        for(var i = endInd; i >= startInd; i--)
            list.remove(i);
    }

    public Iterable<Undoable> getHistory() {
        return actions;
    }
}

