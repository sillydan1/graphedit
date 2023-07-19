package dk.gtz.graphedit.viewmodel;

public interface IFocusable {
    void addFocusListener(Runnable focusEventHandler);
    void focus();
}

