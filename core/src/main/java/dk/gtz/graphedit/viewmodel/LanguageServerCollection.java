package dk.gtz.graphedit.viewmodel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import dk.gtz.graphedit.spi.ILanguageServer;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * A container that holds {@link ILanguageServer} instances.
 */
public class LanguageServerCollection implements ObservableMap<String,ILanguageServer> {
    private final ObservableMap<String,ILanguageServer> servers;
    /**
     * Constructs a new language server collection instance.
     */
    public LanguageServerCollection() {
        servers = FXCollections.observableHashMap();
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

	@Override
	public int size() {
        return servers.size();
	}

	@Override
	public boolean isEmpty() {
        return servers.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
        return servers.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
        return servers.containsValue(value);
	}

	@Override
	public ILanguageServer get(Object key) {
        return servers.get(key);
	}

	@Override
	public ILanguageServer put(String key, ILanguageServer value) {
        return servers.put(key, value);
	}

	@Override
	public ILanguageServer remove(Object key) {
        return servers.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends ILanguageServer> m) {
        servers.putAll(m);
	}

	@Override
	public void clear() {
        servers.clear();
	}

	@Override
	public Set<String> keySet() {
        return servers.keySet();
	}

	@Override
	public Collection<ILanguageServer> values() {
        return servers.values();
	}

	@Override
	public Set<Entry<String, ILanguageServer>> entrySet() {
        return servers.entrySet();
	}

	@Override
	public void addListener(InvalidationListener listener) {
        servers.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
        servers.removeListener(listener);
	}

	@Override
	public void addListener(MapChangeListener<? super String, ? super ILanguageServer> listener) {
        servers.addListener(listener);
	}

	@Override
	public void removeListener(MapChangeListener<? super String, ? super ILanguageServer> listener) {
        servers.removeListener(listener);
	}
}
