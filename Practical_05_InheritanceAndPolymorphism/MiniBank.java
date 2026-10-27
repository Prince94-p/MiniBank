public class MiniBank {
    public static void main(String[] args) {
        Account[] accounts = new Account[]{
            new SavingsAccount("Riya", 10000L, 1000L),
            new CurrentAccount("Aarav", 5000L, 10000L),
            new FixedDepositAccount("Meera", 50000L)
        };

        for (Account acc : accounts) {
            System.out.println(acc.getClass().getSimpleName() + " interest rate: " + acc.interestRate());
            if (acc instanceof SavingsAccount sa) {
                System.out.println("  -> Pattern matched SavingsAccount with minBalance: " + sa.getMinBalance());
            }
        }
    }
}
