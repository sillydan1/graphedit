package dk.gtz.graphedit.viewmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.yalibs.yaundo.Undoable;

public class DiffUndoable extends Undoable {
    private static final Logger logger = LoggerFactory.getLogger(DiffUndoable.class);

    public DiffUndoable(String message, ViewModelProjectResourceSnapshot snapshot) {
        this(message, snapshot.getResource(), snapshot.getDiff());
    }

    public DiffUndoable(String message, ViewModelProjectResource resource, ViewModelDiff diff) {
        super(message,
                () -> ViewModelDiff.revert(resource, diff),
                () -> ViewModelDiff.apply(resource, diff));
        // TODO: Debug log here
        logger.trace("undoable: {} {}", message, diff.toString());
    }
}
