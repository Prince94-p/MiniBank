package util;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.annotation.Audit;

/**
 * Practical 14: Reflective audit logger inspecting @Audit annotations on service methods.
 */
public class AuditLogger {
    private static final List<String> AUDIT_ENTRIES = Collections.synchronizedList(new ArrayList<>());
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logMethodCall(Class<?> serviceClass, String methodName, String details) {
        for (Method method : serviceClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.isAnnotationPresent(Audit.class)) {
                Audit audit = method.getAnnotation(Audit.class);
                String label = audit.value().isEmpty() ? methodName : audit.value();
                String entry = "[" + LocalDateTime.now().format(FMT) + "] @Audit(" + label + ") called on "
                        + serviceClass.getSimpleName() + "." + methodName + "() | " + details;
                AUDIT_ENTRIES.add(entry);
                break;
            }
        }
    }

    public static List<String> getAuditEntries() {
        return new ArrayList<>(AUDIT_ENTRIES);
    }
}
