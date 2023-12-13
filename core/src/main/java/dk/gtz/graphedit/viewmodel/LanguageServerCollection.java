package dk.gtz.graphedit.viewmodel;

import java.util.Collection;
import java.util.HashMap;

import dk.gtz.graphedit.spi.ILanguageServer;

/**
 * A container that holds {@link ILanguageServer} instances.
 */
public class LanguageServerCollection extends HashMap<String,ILanguageServer> {
    /**
     * Constructs a new language server collection instance.
     */
    public LanguageServerCollection() {
        super();
    }

    /**
     * Add a new language server to the collection
     * @param server The server instance to add
     */
    public void add(ILanguageServer server) {
        put(server.getLanguageName(), server);
    }

    /**
     * Add a collection of language servers to the collection
     * @param servers A collection of servers to add
     */
    public void add(Collection<ILanguageServer> servers) {
        for(var factory : servers)
            add(factory);
    }

    /**
     * Add a list of language servers to the collection
     * @param servers Varargs list of servers to add
     */
    public void add(ILanguageServer... servers) {
        for(var factory : servers)
            add(factory);
    }
}
