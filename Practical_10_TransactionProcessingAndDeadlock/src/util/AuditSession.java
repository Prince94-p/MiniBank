package util;

/**
 * Practical 8: Custom AutoCloseable resource used to demonstrate try-with-resources.
 */
public class AuditSession implements AutoCloseable {
    private final String operationName;

    public AuditSession(String operationName) {
        this.operationName = operationName;
        System.out.println("[AuditSession] Opened session for: " + operationName);
    }

    public void recordStep(String message) {
        System.out.println("[AuditSession] " + operationName + " -> " + message);
    }

    @Override
    public void close() {
        System.out.println("[AuditSession] Closed session for: " + operationName);
    }
}
