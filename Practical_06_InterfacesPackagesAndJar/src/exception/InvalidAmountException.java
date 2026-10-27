package exception;

/**
 * Practical 8: Thrown when a deposit, withdrawal, or transfer amount is zero or negative.
 */
public class InvalidAmountException extends BankException {
    private static final long serialVersionUID = 1L;

    public InvalidAmountException(String message) {
        super(message);
    }
}
