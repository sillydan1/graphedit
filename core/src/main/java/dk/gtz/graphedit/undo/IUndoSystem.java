package dk.gtz.graphedit.undo;

public interface IUndoSystem {
    Iterable<Undoable> getHistory();
    void push(Undoable action);
    void undo();
    void redo();
}

