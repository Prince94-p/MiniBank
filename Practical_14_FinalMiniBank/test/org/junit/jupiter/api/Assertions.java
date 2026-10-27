package org.junit.jupiter.api;

/**
 * Standard JUnit 5 Assertions utility methods.
 */
public class Assertions {

    @FunctionalInterface
    public interface Executable {
        void execute() throws Throwable;
    }

    public static void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " | Expected: " + expected + ", Actual: " + actual);
        }
    }

    public static void assertEquals(long expected, long actual) {
        assertEquals(expected, actual, "Values are not equal");
    }

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable actualException) {
            if (expectedType.isInstance(actualException)) {
                return expectedType.cast(actualException);
            }
            throw new AssertionError("Expected exception " + expectedType.getName()
                    + " but threw " + actualException.getClass().getName(), actualException);
        }
        throw new AssertionError("Expected " + expectedType.getName() + " to be thrown, but nothing was thrown.");
    }
}
