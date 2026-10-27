import exception.BankException;
import exception.InsufficientFundsException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import model.Account;
import org.junit.jupiter.api.Test;
import service.AccountWorker;
import service.BankService;
import service.Repository;
import util.AccountFileStore;
import util.ReportGenerator;
import util.TransactionLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Practical 14: JUnit 5 test class verifying BankService and Account concurrency:
 * 1. A deposit increases the balance.
 * 2. A withdrawal exceeding the permitted limit throws an exception.
 * 3. Multiple concurrent deposits maintain the correct balance.
 */
public class BankServiceTest {

    private BankService createIsolatedBankService() {
        Account.resetCounter(0);
        return new BankService(
                new Repository<>(),
                new AccountFileStore("test_accounts.dat"),
                new TransactionLogger(Paths.get("test_logs", "test_tx.log")),
                new ReportGenerator(Paths.get("test_logs"), Paths.get("test_reports", "test_summary.txt"))
        );
    }

    @Test
    public void testDepositIncreasesBalance() throws BankException {
        BankService service = createIsolatedBankService();
        Account acc = service.openAccount("SAVINGS", "Riya", "9876543210", 5000L);
        service.deposit(acc.getAccountNumber(), 2000L);
        assertEquals(7000L, acc.getBalance(), "Deposit of 2000 onto 5000 should produce 7000");
    }

    @Test
    public void testWithdrawalExceedingLimitThrowsException() throws BankException {
        BankService service = createIsolatedBankService();
        Account acc = service.openAccount("SAVINGS", "Riya", "9876543210", 1000L);
        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> service.withdraw(acc.getAccountNumber(), 5000L)
        );
        // SavingsAccount created by BankService has minBalance=500, or if tested directly on Account:
        assertEquals(1000L, acc.getBalance(), "Failed withdrawal must leave balance unchanged at 1000");
    }

    @Test
    public void testConcurrentDepositsMaintainCorrectBalance() throws Exception {
        BankService service = createIsolatedBankService();
        Account acc = service.openAccount("SAVINGS", "ConcurrentOwner", "9876543210", 0L);

        int numThreads = 10;
        int depositsPerThread = 1000;
        long amountPerDeposit = 1L;

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(
                    new AccountWorker(acc, depositsPerThread, amountPerDeposit, false),
                    "TestWorker-" + (i + 1)
            );
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }

        assertEquals(10000L, acc.getBalance(), "10 threads x 1000 deposits of Rs. 1 must equal Rs. 10000");
    }

    /**
     * Standalone reflective test runner that executes all @Test methods.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== Running JUnit 5 @Test Suite: BankServiceTest ===");
        BankServiceTest instance = new BankServiceTest();
        int passed = 0;
        int failed = 0;

        for (Method method : BankServiceTest.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                try {
                    method.invoke(instance);
                    System.out.println("  [PASS] @Test " + method.getName() + "()");
                    passed++;
                } catch (Throwable t) {
                    Throwable cause = t.getCause() != null ? t.getCause() : t;
                    System.out.println("  [FAIL] @Test " + method.getName() + "() -> " + cause.getMessage());
                    failed++;
                }
            }
        }
        System.out.println("=== Test Summary: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
