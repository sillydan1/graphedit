package dk.gtz.graphedit.viewmodel;

import java.util.HashMap;

import dk.gtz.graphedit.view.DemoSyntaxFactory;
import dk.gtz.graphedit.view.ISyntaxFactory;

public final class SyntaxFactoryCollection extends HashMap<String,ISyntaxFactory> {
    public SyntaxFactoryCollection() {
        add(new DemoSyntaxFactory());
    }

    public void add(ISyntaxFactory factory) {
        put(factory.getSyntaxName(), factory);
    }
}

