package model;

/**
 * Practical 5: FixedDepositAccount subclass with 7.0% interest and locked withdrawals.
 */
public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String ownerName, long openingBalance) {
        super(ownerName, openingBalance);
    }

    public FixedDepositAccount(String ownerName) {
        super(ownerName, 0L);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return false; // Fixed deposit is locked
    }

    @Override
    protected long calculateShortfall(long amount) {
        return amount; // Entire withdrawal is disallowed because deposit is locked
    }
}
