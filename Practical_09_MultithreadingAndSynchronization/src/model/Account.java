package model;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.io.Serializable;
import java.util.Objects;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;

/**
 * Practical 2, 3, 5, 6, 7, 8, 9, 10, 11, 12: Abstract Account class implementing
 * Transactable, InterestBearing, Comparable<Account>, and Serializable.
 */
public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;
    private static int counter = 0;

    @Id
    private final String accountNumber;

    @MaxLength(30)
    private String ownerName;

    @Positive(message = "must be > 0")
    private long balance;

    private boolean active;

    private static synchronized String generateAccountNumber() {
        return String.format("AC%04d", ++counter);
    }

    public static synchronized void resetCounter(int value) {
        counter = value;
    }

    public static synchronized void syncCounterWith(String accountNumber) {
        if (accountNumber != null && accountNumber.startsWith("AC")) {
            try {
                int num = Integer.parseInt(accountNumber.substring(2));
                if (num > counter) {
                    counter = num;
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0L);
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    protected long calculateShortfall(long amount) {
        return Math.max(0L, amount - this.balance);
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than 0 (received: " + amount + ")");
        }
        this.balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than 0 (received: " + amount + ")");
        }
        if (!canWithdraw(amount)) {
            long shortfall = calculateShortfall(amount);
            throw new InsufficientFundsException("Withdrawal failed: short by " + shortfall, shortfall);
        }
        this.balance -= amount;
        return true;
    }

    /**
     * Practical 8 & 10: Transfer funds to another account using try-catch-finally,
     * rethrowing BankException when required, and acquiring locks in consistent
     * accountNumber order to prevent deadlocks.
     */
    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new BankException("Target account cannot be null");
        }
        if (this.equals(to)) {
            throw new BankException("Cannot transfer to the same account (" + accountNumber + ")");
        }

        Account firstLock = this.accountNumber.compareTo(to.accountNumber) < 0 ? this : to;
        Account secondLock = firstLock == this ? to : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                boolean withdrawn = false;
                try {
                    this.withdraw(amount);
                    withdrawn = true;
                    to.deposit(amount);
                } catch (BankException e) {
                    if (withdrawn) {
                        this.balance += amount; // Rollback on failure
                    }
                    throw new BankException("Transfer failed from " + this.accountNumber
                            + " to " + to.accountNumber + ": " + e.getMessage(), e);
                } finally {
                    // Practical 8: finally block executes regardless of transfer outcome
                }
            }
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + getBalance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }
}
