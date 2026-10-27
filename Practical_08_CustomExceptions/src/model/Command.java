package model;

/**
 * Practical 4: Record holding parsed transaction command components.
 */
public record Command(TransactionType type, String accountNumber, long amount) {
}
