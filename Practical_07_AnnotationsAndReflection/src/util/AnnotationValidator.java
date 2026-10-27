package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import model.annotation.MaxLength;
import model.annotation.Positive;

/**
 * Practical 7: Reflection-based validator inspecting @Positive and @MaxLength annotations.
 */
public class AnnotationValidator {
    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[]{"Target object is null"};
        }
        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        // Traverse class hierarchy so inherited fields on Account are also validated
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    if (field.isAnnotationPresent(Positive.class)) {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (value instanceof Number num && num.doubleValue() <= 0) {
                            Positive pos = field.getAnnotation(Positive.class);
                            errors.add(pos.message());
                        }
                    }
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        MaxLength maxLen = field.getAnnotation(MaxLength.class);
                        if (value instanceof String str && str.length() > maxLen.value()) {
                            errors.add(field.getName() + " length must be <= " + maxLen.value());
                        }
                    }
                } catch (IllegalAccessException e) {
                    errors.add("Cannot access field " + field.getName() + ": " + e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return errors.toArray(new String[0]);
    }
}
