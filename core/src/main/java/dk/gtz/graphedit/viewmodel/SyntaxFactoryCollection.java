package dk.gtz.graphedit.viewmodel;

import java.util.Collection;
import java.util.HashMap;

import dk.gtz.graphedit.internal.DemoSyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxFactory;

/**
 * Collection type of syntax factories.
 */
public final class SyntaxFactoryCollection extends HashMap<String,ISyntaxFactory> {
    /**
     * Construct a new instance with one {@link DemoSyntaxFactory} element
     */
    public SyntaxFactoryCollection() {
        add(new DemoSyntaxFactory());
    }

    /**
     * Add a new syntax factory to the collection
     * @param factory The factory instance to add
     */
    public void add(ISyntaxFactory factory) {
        put(factory.getSyntaxName(), factory);
    }

    /**
     * Add a collection of syntax factories to the collection
     * @param factories A collection of factires to add
     */
    public void add(Collection<ISyntaxFactory> factories) {
        for(var factory : factories)
            add(factory);
    }

    /**
     * Add a list of syntax factories to the collection
     * @param factories Varargs list of factories to add
     */
    public void add(ISyntaxFactory... factories) {
        for(var factory : factories)
            add(factory);
    }
}
