package model;

/**
 * Practical 5: CurrentAccount subclass supporting an overdraftLimit and 0.0% interest.
 */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;

    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 5000L);
    }

    public CurrentAccount(String ownerName) {
        this(ownerName, 0L, 5000L);
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public synchronized boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= -overdraftLimit;
    }

    @Override
    protected synchronized long calculateShortfall(long amount) {
        long maxWithdrawable = getBalance() + overdraftLimit;
        return Math.max(0L, amount - maxWithdrawable);
    }
}
