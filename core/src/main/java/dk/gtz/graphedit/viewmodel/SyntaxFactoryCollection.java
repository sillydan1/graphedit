package dk.gtz.graphedit.viewmodel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import dk.gtz.graphedit.internal.DemoSyntaxFactory;
import dk.gtz.graphedit.spi.ISyntaxFactory;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * Collection type of syntax factories.
 */
public final class SyntaxFactoryCollection implements ObservableMap<String,ISyntaxFactory> {
    private final ObservableMap<String,ISyntaxFactory> factories;
    /**
     * Construct a new instance with one {@link DemoSyntaxFactory} element
     */
    public SyntaxFactoryCollection() {
	factories = FXCollections.observableHashMap();
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

    @Override
    public int size() {
	return factories.size();
    }

    @Override
    public boolean isEmpty() {
	return factories.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
	return factories.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
	return factories.containsValue(value);
    }

    @Override
    public ISyntaxFactory get(Object key) {
	return factories.get(key);
    }

    @Override
    public ISyntaxFactory put(String key, ISyntaxFactory value) {
	return factories.put(key, value);
    }

    @Override
    public ISyntaxFactory remove(Object key) {
	return factories.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends ISyntaxFactory> m) {
	factories.putAll(m);
    }

    @Override
    public void clear() {
	factories.clear();
    }

    @Override
    public Set<String> keySet() {
	return factories.keySet();
    }

    @Override
    public Collection<ISyntaxFactory> values() {
	return factories.values();
    }

    @Override
    public Set<Entry<String, ISyntaxFactory>> entrySet() {
	return factories.entrySet();
    }

    @Override
    public void addListener(InvalidationListener listener) {
	factories.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
	factories.removeListener(listener);
    }

    @Override
    public void addListener(MapChangeListener<? super String, ? super ISyntaxFactory> listener) {
	factories.addListener(listener);
    }

    @Override
    public void removeListener(MapChangeListener<? super String, ? super ISyntaxFactory> listener) {
	factories.removeListener(listener);
    }

    /**
     * Add a change listener to the collection.
     * This will be invoked when the collection changes.
     * @param listener The listener to add
     */
    public void addChangeListener(MapChangeListener<? super String, ? super ISyntaxFactory> listener) {
	factories.addListener(listener);
    }

    /**
     * Remove a change listener from the collection.
     * @param listener The listener to remove
     */
    public void removeChangeListener(MapChangeListener<? super String, ? super ISyntaxFactory> listener) {
	factories.removeListener(listener);
    }
}
