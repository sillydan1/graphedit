package dk.gtz.graphedit.skyhook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.exceptions.NotFoundException;
import dk.gtz.graphedit.view.util.IAction;

public class DI {
    private static Logger logger = LoggerFactory.getLogger(DI.class);
    private static Map<Class<?>,Object> dependencies = new HashMap<>();
    private static Map<Class<?>,Supplier<Object>> suppliers = new HashMap<>();
    private static Map<String,Object> namedDependencies = new HashMap<>();
    private static Map<String,Supplier<Object>> namedSuppliers = new HashMap<>();
    private static Map<Class<?>,List<IAction>> onAddActions = new HashMap<>();

    public static <T> void add(Class<? extends T> key, T obj) {
        dependencies.put(key, obj);
        if(onAddActions.containsKey(key))
            onAddActions.get(key).forEach(a -> {
                try {
                    a.run(obj);
                } catch(Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });
    }

    public static <T> void add(Class<? extends T> key, Supplier<Object> supplier) {
        suppliers.put(key, supplier);
    }

    public static <T> void add(String key, T obj) {
        namedDependencies.put(key, obj);
    }

    public static void add(String key, Supplier<Object> supplier) {
        namedSuppliers.put(key, supplier);
    }

    public static <T> T get(Class<? super T> key) throws NotFoundException {
        if(dependencies.containsKey(key))
            return (T)dependencies.get(key);
        if(suppliers.containsKey(key))
            return (T)suppliers.get(key).get();
        throw new NotFoundException("DI unable to resolve class '%s'".formatted(key.getName()));
    }

    public static <T> T get(String key) throws NotFoundException {
        if(namedDependencies.containsKey(key))
            return (T)namedDependencies.get(key);
        if(namedSuppliers.containsKey(key))
            return (T)namedSuppliers.get(key).get();
        throw new NotFoundException("DI unable to resolve named dependency '%s'".formatted(key));
    }

    public static <T> boolean contains(Class<? super T> key) {
        if(dependencies.containsKey(key))
            return true;
        if(suppliers.containsKey(key))
            return true;
        return false;
    }

    public static boolean contains(String key) {
        if(namedDependencies.containsKey(key))
            return true;
        if(namedSuppliers.containsKey(key))
            return true;
        return false;
    }

    public static <T> void onAdd(Class<? extends T> key, IAction action) {
        if(!onAddActions.containsKey(key))
            onAddActions.put(key, new ArrayList<IAction>());
        onAddActions.get(key).add(action);
    }
}

