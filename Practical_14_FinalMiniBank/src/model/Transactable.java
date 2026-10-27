package model;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;

/**
 * Practical 6 & 8: Interface for transactional operations on an account.
 */
public interface Transactable {
    void deposit(long amount) throws InvalidAmountException;
    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}
