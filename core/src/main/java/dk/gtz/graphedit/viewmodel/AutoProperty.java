package dk.gtz.graphedit.viewmodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;

public abstract class AutoProperty<T extends Property<T>> implements Property<T> {
    private static Logger logger = LoggerFactory.getLogger(AutoProperty.class);
    private T value;
    private List<Field> fields;
    private final Map<Field, ChangeListener<? super Object>> changeListeners;
    private final Map<Field, InvalidationListener> invalidationListeners;

    protected AutoProperty() {
        this.changeListeners = new HashMap<>();
        this.invalidationListeners = new HashMap<>();
    }

    protected void loadFields(Class<?> clazz, T value) {
        this.value = value;
        fields = new ArrayList<>();
        for(var field : clazz.getFields())
            if(List.of(field.getAnnotations())
                    .stream()
                    .anyMatch(a -> a
                        .annotationType()
                        .getSimpleName()
                        .equals(Autolisten.class.getSimpleName())))
                fields.add(field);
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        for(var field : fields) {
            var isObservable = ObservableValue.class.isAssignableFrom(field.getType());
            if(!isObservable)
                continue;
            try {
                var observable = (ObservableValue<?>)field.get(value);
                if(!changeListeners.containsKey(field))
                    changeListeners.put(field, (e,o,n) -> listener.changed(value, value, value));
                observable.addListener(changeListeners.get(field));
            } catch (IllegalAccessException e) {
                logger.error("error adding listener '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        for(var field : fields) {
            var isObservable = ObservableValue.class.isAssignableFrom(field.getType());
            if(!isObservable)
                continue;
            try {
                var observable = (ObservableValue<?>)field.get(value);
                if(changeListeners.containsKey(field))
                    observable.addListener(changeListeners.get(field));
                else // BUG: This probably doesnt work, but it doesn't hurt to try
                    observable.addListener((e,o,n) -> listener.changed(value, value, value));
            } catch (IllegalAccessException e) {
                logger.error("error removing listener '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        for(var field : fields) {
            if(!field.getType().isAssignableFrom(ObservableValue.class))
                continue;
            try {
                var observable = (ObservableValue<?>)field.get(value);
                if(!invalidationListeners.containsKey(field))
                    invalidationListeners.put(field, listener);
                observable.addListener(invalidationListeners.get(field));
            } catch (IllegalAccessException e) {
                logger.error("error adding invalidation listener '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        for(var field : fields) {
            var isObservable = ObservableValue.class.isAssignableFrom(field.getType());
            if(!isObservable)
                continue;
            try {
                var observable = (ObservableValue<?>)field.get(value);
                if(invalidationListeners.containsKey(field))
                    observable.addListener(invalidationListeners.get(field));
                else // BUG: This probably doesnt work, but it doesn't hurt to try
                    observable.addListener(listener);
            } catch (IllegalAccessException e) {
                logger.error("error removing invalidation listener '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void bind(ObservableValue<? extends T> observable) {
        for(var field : fields) {
            if(!field.getType().isAssignableFrom(Property.class))
                continue;
            try {
                var thisObservable = (Property)field.get(value);
                var thatObservable = (Property)field.get(observable.getValue());
                thisObservable.bind(thatObservable);
            } catch (IllegalAccessException e) {
                logger.error("error binding '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void unbind() {
        for(var field : fields) {
            var isProperty = Property.class.isAssignableFrom(field.getType());
            if(!isProperty)
                continue;
            try {
                ((Property<?>)field.get(value)).unbind();
            } catch (IllegalAccessException e) {
                logger.error("error binding '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isBound() {
        for(var field : fields) {
            var isProperty = Property.class.isAssignableFrom(field.getType());
            if(!isProperty)
                continue;
            try {
                if(((Property<?>)field.get(value)).isBound())
                    return true;
            } catch (IllegalAccessException e) {
                logger.error("isBound error '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void bindBidirectional(Property<T> other) {
        for(var field : fields) {
            var isProperty = Property.class.isAssignableFrom(field.getType());
            if(!isProperty)
                continue;
            try {
                var thisObservable = (Property)field.get(value);
                var thatObservable = (Property)field.get(other);
                thisObservable.bindBidirectional(thatObservable);
            } catch (IllegalAccessException e) {
                logger.error("error binding bidirectionally '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void unbindBidirectional(Property<T> other) {
        for(var field : fields) {
            var isProperty = Property.class.isAssignableFrom(field.getType());
            if(!isProperty)
                continue;
            try {
                var thisObservable = (Property)field.get(value);
                var thatObservable = (Property)field.get(other);
                thisObservable.unbindBidirectional(thatObservable);
            } catch (IllegalAccessException e) {
                logger.error("error unbinding bidirectionally '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setValue(T value) {
        for(var field : fields) {
            var isWritable = WritableObjectValue.class.isAssignableFrom(field.getType());
            if(!isWritable)
                continue;
            try {
                var thisObservable = (WritableObjectValue)field.get(this.value);
                var thatObservable = (WritableObjectValue)field.get(value);
                thisObservable.set(thatObservable.get());
            } catch (IllegalAccessException e) {
                logger.error("error setting value '{}': {}", field.getName(), e.getMessage(), e);
            }
        }
    }
}
