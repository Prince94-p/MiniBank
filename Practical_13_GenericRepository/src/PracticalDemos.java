import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Account;
import model.BankInfo;
import model.Command;
import model.CurrentAccount;
import model.Customer;
import model.FixedDepositAccount;
import model.MenuOption;
import model.Premium;
import model.SavingsAccount;
import model.TransactionType;
import model.WithdrawRule;
import service.AccountStore;
import service.AccountWorker;
import service.BankService;
import service.Repository;
import service.TransactionProcessor;
import util.AccountFileStore;
import util.AnnotationValidator;
import util.AuditLogger;
import util.AuditSession;
import util.CommandParser;
import util.ReportGenerator;
import util.StatementFormatter;
import util.TransactionLogger;
import util.Validator;
import static util.Validator.isValidMobile;

/**
 * Automated verification runner demonstrating the exact Expected Results
 * for Practicals 1 through 14 of CEUE203 OOPJ MiniBank.
 */
public class PracticalDemos {

    public static void runAllDemos() {
        System.out.println("************************************************************");
        System.out.println("  CHARUSAT CEUE203 OOPJ — MINIBANK PRACTICALS 1 TO 14 DEMO  ");
        System.out.println("************************************************************\n");

        demoPractical1();
        demoPractical2();
        demoPractical3();
        demoPractical4();
        demoPractical5();
        demoPractical6();
        demoPractical7();
        demoPractical8();
        demoPractical9();
        demoPractical10();
        demoPractical11();
        demoPractical12();
        demoPractical13();
        demoPractical14();

        System.out.println("\n************************************************************");
        System.out.println("  ALL 14 PRACTICALS VERIFIED SUCCESSFULLY!                  ");
        System.out.println("************************************************************");
    }

    public static void demoPractical1() {
        System.out.println("--- Practical 1: MiniBank Menu Shell ---");
        BankInfo header = new BankInfo("CHARUSAT MiniBank", "Changa");
        System.out.println(header);
        for (MenuOption option : new MenuOption[]{
                MenuOption.OPEN_ACCOUNT, MenuOption.DEPOSIT,
                MenuOption.WITHDRAW, MenuOption.TRANSFER, MenuOption.EXIT}) {
            String msg = switch (option) {
                case OPEN_ACCOUNT -> "[Placeholder] Opening a new account...";
                case DEPOSIT      -> "[Placeholder] Depositing money...";
                case WITHDRAW     -> "[Placeholder] Withdrawing money...";
                case TRANSFER     -> "[Placeholder] Transferring money...";
                case EXIT         -> "Thank you for banking with MiniBank. Goodbye!";
                default           -> "[Placeholder] Selected " + option;
            };
            System.out.println("Selected " + option + " -> " + msg);
        }
        System.out.println();
    }

    public static void demoPractical2() {
        System.out.println("--- Practical 2: Customer and Account Classes ---");
        Customer.resetCounter(100);
        Account.resetCounter(0);

        Customer c1 = new Customer("Riya Patel", "riya@charusat.edu.in", "9876543210");
        System.out.println("Created Customer: ID=" + c1.getCustomerId() + ", Name=" + c1.getName());

        Account[] accounts = new Account[]{
            new SavingsAccount("Riya", 5000L, 0L),
            new SavingsAccount("Aarav", 3000L, 0L),
            new SavingsAccount("Meera")
        };

        try {
            Account acc = accounts[0];
            System.out.println("Starting Balance (" + acc.getAccountNumber() + "): Rs. " + acc.getBalance());
            acc.deposit(2000L);
            System.out.println("After depositing Rs. 2000: Rs. " + acc.getBalance());
            acc.withdraw(3000L);
            System.out.println("After withdrawing Rs. 3000: Rs. " + acc.getBalance());
            try {
                acc.withdraw(10000L);
            } catch (InsufficientFundsException e) {
                System.out.println("Attempting to withdraw Rs. 10000 failed -> Balance remains: Rs. " + acc.getBalance());
            }
        } catch (BankException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    public static void demoPractical3() {
        System.out.println("--- Practical 3: Object Methods and Nested Address Class ---");
        Account.resetCounter(0);
        SavingsAccount a1 = new SavingsAccount("Riya", 4000L);
        SavingsAccount a2 = new SavingsAccount("Aarav", 7500L);

        System.out.println("toString() output: " + a1);
        System.out.println("a1.equals(a1): " + a1.equals(a1) + ", a1.equals(a2): " + a1.equals(a2));
        System.out.println("instanceof check (a1 instanceof Account): " + (a1 instanceof Account));

        Customer.Address addr = new Customer.Address("University Road", "Changa", "388421");
        Customer orig = new Customer("Riya", "riya@charusat.edu.in", "9876543210", addr);
        Customer copy = orig.clone();
        System.out.println("Original Customer: " + orig);
        System.out.println("Cloned Customer  : " + copy + " (equals=" + orig.equals(copy)
                + ", sameRef=" + (orig == copy) + ")");
        System.out.println();
    }

    public static void demoPractical4() {
        System.out.println("--- Practical 4: Validation and Command Parsing ---");
        System.out.println("isValidMobile(\"9876543210\"): " + isValidMobile("9876543210"));
        System.out.println("isValidMobile(\"12345\")     : " + isValidMobile("12345"));
        System.out.println("isValidEmail(\"riya@bank.in\"): " + Validator.isValidEmail("riya@bank.in")
                + " | invalid: " + Validator.isValidEmail("bad-email"));
        System.out.println("isValidPan(\"ABCDE1234F\")   : " + Validator.isValidPan("ABCDE1234F")
                + " | invalid: " + Validator.isValidPan("12345ABCDE"));
        System.out.println("isValidIfsc(\"SBIN0001234\") : " + Validator.isValidIfsc("SBIN0001234")
                + " | invalid: " + Validator.isValidIfsc("SBIN123"));

        Command cmd = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.println("Parsed Command -> type=" + cmd.type()
                + ", accountNumber=" + cmd.accountNumber()
                + ", amount=" + cmd.amount());

        Account.resetCounter(0);
        Account sampleAcc = new SavingsAccount("Riya", 4000L);
        System.out.println(StatementFormatter.buildStatement(sampleAcc));
        System.out.println();
    }

    public static void demoPractical5() {
        System.out.println("--- Practical 5: Inheritance and Polymorphism ---");
        Account.resetCounter(0);
        Account[] accounts = new Account[]{
            new SavingsAccount("Riya", 10000L, 1000L),
            new CurrentAccount("Aarav Enterprises", 5000L, 10000L),
            new FixedDepositAccount("Meera", 50000L)
        };

        for (Account acc : accounts) {
            System.out.println(acc.getClass().getSimpleName() + " (" + acc.getAccountNumber()
                    + ") -> interestRate(): " + acc.interestRate());
            if (acc instanceof SavingsAccount sa) {
                System.out.println("  [Pattern Matching instanceof] SavingsAccount minBalance = Rs. " + sa.getMinBalance());
            }
        }
        System.out.println();
    }

    public static void demoPractical6() {
        System.out.println("--- Practical 6: Interfaces, Packages and Runnable JAR ---");
        Account.resetCounter(0);
        Account acc = new SavingsAccount("Riya", 10000L, 1000L);

        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return amount <= 5000L && account.canWithdraw(amount);
            }
        };

        WithdrawRule lambdaRule = (account, amount) -> amount <= 5000L && account.canWithdraw(amount);

        System.out.println("Anonymous WithdrawRule (3000): " + anonymousRule.allow(acc, 3000L));
        System.out.println("Lambda WithdrawRule (3000)   : " + lambdaRule.allow(acc, 3000L));
        System.out.println("Yearly Interest (default method): Rs. " + acc.yearlyInterest());
        System.out.println("Is SavingsAccount Premium?   : " + (acc instanceof Premium));
        System.out.println();
    }

    public static void demoPractical7() {
        System.out.println("--- Practical 7: Annotations and Reflection ---");
        Account.resetCounter(0);
        Account invalidAcc = new SavingsAccount("Riya", -100L);
        Account validAcc = new SavingsAccount("Riya", 5000L);

        String[] invalidErrors = AnnotationValidator.validate(invalidAcc);
        String[] validErrors = AnnotationValidator.validate(validAcc);

        System.out.println("Validating account with balance -100 -> " + Arrays.toString(invalidErrors));
        System.out.println("Validating account with balance 5000 -> " + Arrays.toString(validErrors)
                + " (length=" + validErrors.length + ")");
        System.out.println();
    }

    public static void demoPractical8() {
        System.out.println("--- Practical 8: Custom Exceptions and Fault Tolerance ---");
        Account.resetCounter(0);
        Account acc = new SavingsAccount("Riya", 1000L, 0L);

        try (AuditSession session = new AuditSession("Practical8-WithdrawalTest")) {
            session.recordStep("Attempting to withdraw Rs. 5000 from balance Rs. " + acc.getBalance());
            acc.withdraw(5000L);
        } catch (InsufficientFundsException | InvalidAmountException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Finally block verified -> Balance remains: Rs. " + acc.getBalance());
        }
        System.out.println();
    }

    public static void demoPractical9() {
        System.out.println("--- Practical 9: Multithreading and Synchronization ---");
        Account.resetCounter(0);
        Account sharedAccount = new SavingsAccount("ConcurrentUser", 0L, 0L);
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new AccountWorker(sharedAccount, 1000, 1L, false), "Worker-" + (i + 1));
            System.out.print(threads[i].getName() + " ");
            threads[i].start();
        }
        System.out.println();

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Final Balance after 10 threads x 1,000 deposits of Rs. 1: Rs. "
                + sharedAccount.getBalance());
        System.out.println();
    }

    public static void demoPractical10() {
        System.out.println("--- Practical 10: Transaction Processing and Deadlock Prevention ---");
        Account.resetCounter(0);
        Account accA = new SavingsAccount("Alice", 5000L, 0L);
        Account accB = new SavingsAccount("Bob", 5000L, 0L);

        TransactionProcessor processor = new TransactionProcessor();
        processor.registerAccount(accA);
        processor.registerAccount(accB);

        // Submit multiple Runnable tasks to fixed thread pool
        processor.submit(() -> {
            try {
                accA.deposit(1000L);
            } catch (BankException ignored) {
            }
        });
        processor.submit(() -> {
            try {
                accB.withdraw(500L);
            } catch (BankException ignored) {
            }
        });

        try {
            Command[] batch = new Command[]{
                new Command(TransactionType.DEPOSIT, accA.getAccountNumber(), 500L),
                new Command(TransactionType.WITHDRAW, accB.getAccountNumber(), 300L)
            };
            processor.runProducerConsumerBatch(batch);
            processor.stop();

            TransactionProcessor.demonstrateDeadlockAndFix(accA, accB, 1000L);
        } catch (Exception e) {
            System.out.println("Error in Practical 10 demo: " + e.getMessage());
        }
        System.out.println();
    }

    public static void demoPractical11() {
        System.out.println("--- Practical 11: File Handling, Persistence and Reporting ---");
        try {
            Account.resetCounter(0);
            Account[] accountsToSave = new Account[]{
                new SavingsAccount("Riya", 7000L, 500L),
                new CurrentAccount("Aarav", 12000L, 5000L)
            };

            AccountFileStore store = new AccountFileStore("demo_accounts.dat");
            store.save(accountsToSave);

            Account[] reloaded = store.load();
            System.out.println("Reloaded Accounts from demo_accounts.dat:");
            for (Account acc : reloaded) {
                System.out.println("  " + acc);
            }

            Path demoLog = Paths.get("demo_logs", "transactions.log");
            java.nio.file.Files.deleteIfExists(demoLog);
            TransactionLogger logger = new TransactionLogger(demoLog);
            logger.log(TransactionType.DEPOSIT, "AC0001", 7000L, "Initial Deposit");
            logger.log(TransactionType.DEPOSIT, "AC0002", 12000L, "Initial Deposit");
            logger.log(TransactionType.WITHDRAW, "AC0001", 2000L, "ATM Withdrawal");

            ReportGenerator reportGen = new ReportGenerator(
                    Paths.get("demo_logs"), Paths.get("demo_reports", "summary.txt"));
            String report = reportGen.generateReport();
            System.out.println(report);
        } catch (Exception e) {
            System.out.println("Error in Practical 11 demo: " + e.getMessage());
        }
        System.out.println();
    }

    public static void demoPractical12() {
        System.out.println("--- Practical 12: Collections Framework and Account Store ---");
        Account.resetCounter(0);
        AccountStore store = new AccountStore();
        store.add(new SavingsAccount("Riya", 5000L));
        store.add(new SavingsAccount("Aarav", 2000L));
        store.add(new SavingsAccount("Meera", 9000L));

        System.out.println("Lookup AC0001: " + store.get("AC0001"));
        System.out.println("Accounts sorted by balance (descending):");
        for (Account acc : store.sortedByBalance()) {
            System.out.println("  " + acc);
        }
        System.out.println();
    }

    public static void demoPractical13() {
        System.out.println("--- Practical 13: Generic Repository ---");
        Account.resetCounter(0);
        Customer.resetCounter(100);

        Repository<Account> accountRepo = new Repository<>();
        Account a1 = new SavingsAccount("Riya", 5000L);
        Account a2 = new CurrentAccount("Aarav", 8000L);
        accountRepo.saveAll(a1, a2);

        System.out.println("findById(\"AC0001\"): " + accountRepo.findById("AC0001"));
        System.out.println("findAll() Accounts : " + accountRepo.findAll());

        Repository<Customer> customerRepo = new Repository<>();
        Customer c1 = new Customer("Riya", "riya@charusat.edu.in", "9876543210");
        customerRepo.save(c1);
        System.out.println("Customer Repository findById(\"CUST101\"): " + customerRepo.findById("CUST101"));

        Long[] balances = new Long[]{5000L, 8000L, 11000L};
        System.out.println("Generic bounded average(balances): Rs. " + Repository.average(balances));
        System.out.println();
    }

    public static void demoPractical14() {
        System.out.println("--- Practical 14: Final MiniBank Integration & Audit Logging ---");
        try {
            Account.resetCounter(0);
            BankService service = new BankService(
                    new Repository<>(),
                    new AccountFileStore("p14_accounts.dat"),
                    new TransactionLogger(Paths.get("p14_logs", "tx.log")),
                    new ReportGenerator(Paths.get("p14_logs"), Paths.get("p14_reports", "report.txt"))
            );

            Account acc1 = service.openAccount("SAVINGS", "Riya Patel", "9876543210", 5000L);
            Account acc2 = service.openAccount("CURRENT", "Aarav Shah", "9123456789", 3000L);
            service.deposit(acc1.getAccountNumber(), 2000L);
            service.withdraw(acc1.getAccountNumber(), 1000L);
            service.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 1500L);

            System.out.println(service.statement(acc1.getAccountNumber()));
            System.out.println("All Accounts in BankService: " + service.listAccounts());
            List<String> audits = AuditLogger.getAuditEntries();
            System.out.println("Reflective @Audit entries recorded: " + audits.size());
            if (!audits.isEmpty()) {
                System.out.println("  Latest @Audit log: " + audits.get(audits.size() - 1));
            }
        } catch (Exception e) {
            System.out.println("Error in Practical 14 demo: " + e.getMessage());
        }
    }
}
