package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import model.annotation.Id;

/**
 * Practical 13: Generic Repository<T> using reflection to inspect @Id fields,
 * @SafeVarargs batch saving, and bounded type parameter average calculation.
 */
public class Repository<T> {
    private final HashMap<String, T> storage = new HashMap<>();

    public synchronized T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        String key = extractId(entity);
        storage.put(key, entity);
        return entity;
    }

    @SafeVarargs
    public final synchronized void saveAll(T... items) {
        for (T item : items) {
            if (item != null) {
                save(item);
            }
        }
    }

    public synchronized T findById(String id) {
        return storage.get(id);
    }

    public synchronized ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public synchronized boolean delete(String id) {
        return storage.remove(id) != null;
    }

    public synchronized void clear() {
        storage.clear();
    }

    private String extractId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);
                        if (value == null) {
                            throw new IllegalStateException("@Id field '" + field.getName() + "' is null");
                        }
                        return value.toString();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Unable to read @Id field: " + field.getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        throw new IllegalArgumentException("Class " + entity.getClass().getName()
                + " has no field annotated with @Id");
    }

    /**
     * Practical 13: Generic method with bounded type parameter <N extends Number>.
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (N val : values) {
            if (val != null) {
                sum += val.doubleValue();
            }
        }
        return sum / values.length;
    }
}
