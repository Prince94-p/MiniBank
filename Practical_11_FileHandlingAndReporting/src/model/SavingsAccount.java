package model;

/**
 * Practical 5 & 6: SavingsAccount subclass with minimum balance requirement and 4.0% interest.
 */
public class SavingsAccount extends Account implements Premium {
    private static final long serialVersionUID = 1L;
    private final long minBalance;

    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        super(ownerName, openingBalance);
        this.minBalance = minBalance;
    }

    public SavingsAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 0L);
    }

    public SavingsAccount(String ownerName) {
        this(ownerName, 0L, 0L);
    }

    public long getMinBalance() {
        return minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public synchronized boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= minBalance;
    }

    @Override
    protected synchronized long calculateShortfall(long amount) {
        long availableToWithdraw = getBalance() - minBalance;
        return Math.max(0L, amount - availableToWithdraw);
    }
}
