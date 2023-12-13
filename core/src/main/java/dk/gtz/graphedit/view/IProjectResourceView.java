package dk.gtz.graphedit.view;

/**
 * Interface for views that are viewing project resources.
 */
public interface IProjectResourceView {
    /**
     * Add an editor to the view
     * @param editor The model editor to add
     */
    public void addEditor(ModelEditorController editor);
}
