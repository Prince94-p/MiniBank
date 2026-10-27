package exception;

/**
 * Practical 8: Thrown when an account lacks sufficient balance for a withdrawal.
 */
public class InsufficientFundsException extends BankException {
    private static final long serialVersionUID = 1L;
    private final long shortfall;

    public InsufficientFundsException(String message, long shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public InsufficientFundsException(long shortfall) {
        this("Withdrawal failed: short by " + shortfall, shortfall);
    }

    public long getShortfall() {
        return shortfall;
    }
}
