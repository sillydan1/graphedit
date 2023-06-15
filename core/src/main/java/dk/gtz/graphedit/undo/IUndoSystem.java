package dk.gtz.graphedit.undo;

public interface IUndoSystem {
    void push(Undoable action);
    void undo();
    void redo();
}

