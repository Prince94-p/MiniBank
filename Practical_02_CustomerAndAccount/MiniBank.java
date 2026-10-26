public class MiniBank {
    public static void main(String[] args) {
        Customer c1 = new Customer("Riya", "riya@charusat.edu.in", "9876543210");
        System.out.println("Customer created: " + c1.getCustomerId() + " - " + c1.getName());

        Account[] accounts = new Account[]{
            new Account("Riya", 5000L),
            new Account("Aarav", 3000L),
            new Account("Meera")
        };

        Account a1 = accounts[0];
        System.out.println("Starting balance: Rs. " + a1.getBalance());
        a1.deposit(2000L);
        System.out.println("After depositing Rs. 2000: Rs. " + a1.getBalance());
        boolean w1 = a1.withdraw(3000L);
        System.out.println("Withdrawing Rs. 3000 (success=" + w1 + "): Rs. " + a1.getBalance());
        boolean w2 = a1.withdraw(10000L);
        System.out.println("Withdrawing Rs. 10000 (success=" + w2 + "): Rs. " + a1.getBalance());

        System.out.println("\nAll Accounts:");
        for (Account acc : accounts) {
            System.out.println(acc.getAccountNumber() + " | " + acc.getOwnerName() + " | Balance: Rs. " + acc.getBalance());
        }
    }
}
