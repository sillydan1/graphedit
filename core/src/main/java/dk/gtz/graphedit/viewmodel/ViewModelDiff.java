package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dk.gtz.graphedit.exceptions.UncomparableException;

public class ViewModelDiff {
    private final String syntaxStyle;
    private final List<ViewModelVertex> vertexDeletions;
    private final List<ViewModelEdge> edgeDeletions;
    private final Map<UUID,ViewModelVertex> vertexAdditions;
    private final Map<UUID,ViewModelEdge> edgeAdditions;

    private ViewModelDiff(String syntaxStyle) {
        this.syntaxStyle = syntaxStyle;
        vertexDeletions = new ArrayList<>();
        edgeDeletions = new ArrayList<>();
        vertexAdditions = new HashMap<>();
        edgeAdditions = new HashMap<>();
    }

    public static ViewModelDiff compare(ViewModelProjectResource a, ViewModelProjectResource b) throws UncomparableException {
        var aSyntaxName = a.getSyntaxName();
        var bSyntaxName = b.getSyntaxName();
        if(aSyntaxName.isEmpty() || bSyntaxName.isEmpty())
            throw new UncomparableException("cannot compare graphs because (at least) one of them dont have a syntax name metadata field");
        if(!aSyntaxName.get().equals(bSyntaxName.get()))
            throw new UncomparableException("cannot compare graphs of different syntaxes '%s' and '%s'".formatted(aSyntaxName.get(), bSyntaxName.get()));
        return compare(aSyntaxName.get(), a.syntax(), b.syntax());
    }

    private static ViewModelDiff compare(String syntaxStyle, ViewModelGraph a, ViewModelGraph b) {
        var result = new ViewModelDiff(syntaxStyle);
        // Sort syntaxes on uuid
        // Do the algorithm (with O(D^2) space)
        return result;
    }

    public static void apply(ViewModelGraph graph, ViewModelDiff diff) {
        // TODO: Check if this actually makes for a valid syntax
        for(var edgeDeletion : diff.edgeDeletions)
            graph.edges().remove(edgeDeletion.id());
        for(var vertexDeletion : diff.vertexDeletions)
            graph.vertices().remove(vertexDeletion.id());
        graph.vertices().putAll(diff.vertexAdditions);
        graph.edges().putAll(diff.edgeAdditions);
        // TODO: add an undoable action
    }
}
