package model;

/**
 * Practical 6 & 7: Functional interface defining a custom withdrawal rule.
 */
@FunctionalInterface
public interface WithdrawRule {
    boolean allow(Account account, long amount);
}
