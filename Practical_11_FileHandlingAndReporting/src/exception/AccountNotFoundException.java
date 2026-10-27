package exception;

/**
 * Practical 8: Thrown when an account number cannot be found in the repository/store.
 */
public class AccountNotFoundException extends BankException {
    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String message) {
        super(message);
    }
}
