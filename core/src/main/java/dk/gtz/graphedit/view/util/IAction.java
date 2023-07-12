package dk.gtz.graphedit.view.util;

@FunctionalInterface
public interface IAction {
    <T> void run(T obj);
}

