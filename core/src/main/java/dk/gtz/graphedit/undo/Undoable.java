package dk.gtz.graphedit.undo;

public class Undoable {
    private final Runnable undoAction;
    private final Runnable redoAction;
    private final String description;

    public Undoable(Runnable undoAction, Runnable redoAction) {
        this("<n/a>", undoAction, redoAction);
    }

    public Undoable(String description, Runnable undoAction, Runnable redoAction) {
        this.redoAction = redoAction;
        this.undoAction = undoAction;
        this.description = description;
    }

    public void redo() {
        redoAction.run();
    }

    public void undo() {
        undoAction.run();
    }

    public String getDescription() {
        return description;
    }
}

