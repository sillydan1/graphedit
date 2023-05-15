package dk.gtz.graphedit.skyhook;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import dk.gtz.graphedit.exceptions.NotFoundException;

public class DI {
    private static Map<Class<?>,Object> dependencies = new HashMap<>();
    private static Map<Class<?>,Supplier<Object>> suppliers = new HashMap<>();

    public static <T> void add(Class<? extends T> key, T obj) {
        dependencies.put(key, obj);
    }

    public static <T> void add(Class<? extends T> key, Supplier<Object> supplier) {
        suppliers.put(key, supplier);
    }

    public static <T> T get(Class<? super T> key) throws NotFoundException {
        if(dependencies.containsKey(key))
            return (T)dependencies.get(key);
        if(suppliers.containsKey(key))
            return (T)suppliers.get(key).get();
        throw new NotFoundException("DI unable to resolve class " + key.getName());
    }
}

