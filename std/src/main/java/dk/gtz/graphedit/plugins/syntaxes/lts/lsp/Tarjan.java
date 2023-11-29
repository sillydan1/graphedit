package dk.gtz.graphedit.plugins.syntaxes.lts.lsp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tarjans Strongly connected components algorithm implementation.
 * See https://sites.cs.ucsb.edu/~gilbert/cs240a/Old/cs240aSpr2011/slides/TarjanDFS.pdf for more info
 * TODO: Consider making a yalibs distribution of this
 * */
public class Tarjan<K, V extends Collection<K>> {
    private static class SCCDecoration {
        public int index;
        public int lowLink;
        public boolean onStack;

        public SCCDecoration(int index, int lowLink, boolean onStack) {
            this.index = index;
            this.lowLink = lowLink;
            this.onStack = onStack;
        }
    }
    private Map<K, SCCDecoration> decorations;
    private int index;

    private Tarjan() {

    }

    private void strongConnect(K key, final Map<K, V> map, Stack<K> stack, List<List<K>> sccs) {
        decorations.put(key, new SCCDecoration(index, index, true));
        var decorationValue = decorations.get(key);
        stack.push(key);
        index++;
        for(var w : map.get(key)) {
            if(!decorations.containsKey(w)) {
                strongConnect(w, map, stack, sccs);
                decorationValue.lowLink = Math.min(decorationValue.lowLink, decorations.get(w).lowLink);
            } else if(decorations.get(w).onStack)
                decorationValue.lowLink = Math.min(decorationValue.lowLink, decorations.get(w).index);
        }
        if (decorationValue.lowLink != decorationValue.index)
            return;
        var scc = new ArrayList<K>();
        var w = stack.peek();
        while(decorations.get(w).index >= decorationValue.index) {
            stack.pop();
            decorations.get(w).onStack = false;
            scc.add(w);
            if(stack.empty())
                break;
            w = stack.peek();
        }
        sccs.add(scc);
    }

    private List<List<K>> getStronglyConnectedComponentsHelper(final Map<K, V> map) {
        var result = new ArrayList<List<K>>();
        decorations = new HashMap<>();
        index = 0;
        for(var k : map.keySet())
            if(!decorations.containsKey(k))
                strongConnect(k, map, new Stack<K>(), result);
        return result;
    }

    public static <K,V extends Collection<K>> List<List<K>> getStronglyConnectedComponents(final Map<K,V> map) {
        return new Tarjan<K,V>().getStronglyConnectedComponentsHelper(map);
    }
}
