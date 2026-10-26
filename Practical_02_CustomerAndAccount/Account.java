public class Account {
    private static int counter = 0;

    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static String generateAccountNumber() {
        return String.format("AC%04d", ++counter);
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

    public void deposit(long amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(long amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public long getBalance() { return balance; }
    public boolean isActive() { return active; }
}
