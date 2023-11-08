package dk.gtz.graphedit.viewmodel;

import java.util.Collection;
import java.util.HashMap;

import dk.gtz.graphedit.internal.DemoSyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxFactory;

public final class SyntaxFactoryCollection extends HashMap<String,ISyntaxFactory> {
    public SyntaxFactoryCollection() {
        add(new DemoSyntaxFactory());
    }

    public void add(ISyntaxFactory factory) {
        put(factory.getSyntaxName(), factory);
    }

    public void add(Collection<ISyntaxFactory> factories) {
        for(var factory : factories)
            add(factory);
    }

    public void add(ISyntaxFactory... factories) {
        for(var factory : factories)
            add(factory);
    }
}

