package dk.gtz.graphedit.viewmodel;

/**
 * Interface for classes that can steal focus
 */
public interface IFocusable {
    /**
     * Add an event listener for when the focus is stolen
     * @param focusEventHandler the handler to call
     */
    void addFocusListener(Runnable focusEventHandler);

    /**
     * Steal the focus
     */
    void focus();
}

