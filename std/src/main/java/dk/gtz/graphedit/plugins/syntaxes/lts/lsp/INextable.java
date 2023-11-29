package dk.gtz.graphedit.plugins.syntaxes.lts.lsp;

import java.util.List;

@FunctionalInterface
public interface INextable<T> {
    List<T> getNext();
}
