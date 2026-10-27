package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Account;
import model.Command;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.TransactionType;
import model.annotation.Audit;
import util.AccountFileStore;
import util.AuditLogger;
import util.ReportGenerator;
import util.StatementFormatter;
import util.TransactionLogger;
import static util.Validator.isValidMobile;

/**
 * Practical 14: Layered BankService integrating Repository<Account>, Validator,
 * custom exceptions, persistence (AccountFileStore, TransactionLogger, ReportGenerator),
 * concurrent TransactionProcessor, and reflective @Audit logging.
 */
public class BankService {
    private final Repository<Account> accountRepository;
    private final AccountFileStore fileStore;
    private final TransactionLogger transactionLogger;
    private final ReportGenerator reportGenerator;
    private final Map<String, List<String>> accountHistories;

    public BankService() {
        this(new Repository<>(), new AccountFileStore(), new TransactionLogger(), new ReportGenerator());
    }

    public BankService(
            Repository<Account> accountRepository,
            AccountFileStore fileStore,
            TransactionLogger transactionLogger,
            ReportGenerator reportGenerator) {
        this.accountRepository = accountRepository;
        this.fileStore = fileStore;
        this.transactionLogger = transactionLogger;
        this.reportGenerator = reportGenerator;
        this.accountHistories = new HashMap<>();
    }

    @Audit("OPEN_ACCOUNT")
    public Account openAccount(String type, String ownerName, String mobile, long openingBalance)
            throws BankException {
        AuditLogger.logMethodCall(BankService.class, "openAccount",
                "type=" + type + ", owner=" + ownerName + ", openingBalance=" + openingBalance);

        if (ownerName == null || ownerName.isBlank()) {
            throw new BankException("Owner name cannot be empty");
        }
        if (mobile != null && !mobile.isBlank() && !isValidMobile(mobile)) {
            throw new BankException("Invalid mobile number: " + mobile + " (Expected 10-digit Indian mobile)");
        }
        if (openingBalance < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative: " + openingBalance);
        }

        Account account = switch (type.trim().toUpperCase()) {
            case "SAVINGS", "S", "1" -> new SavingsAccount(ownerName.trim(), openingBalance, 500L);
            case "CURRENT", "C", "2" -> new CurrentAccount(ownerName.trim(), openingBalance, 5000L);
            case "FD", "FIXED", "3" -> new FixedDepositAccount(ownerName.trim(), openingBalance);
            default -> new SavingsAccount(ownerName.trim(), openingBalance, 0L);
        };

        accountRepository.save(account);
        recordHistory(account.getAccountNumber(),
                "Opened " + account.getClass().getSimpleName() + " with Rs. " + openingBalance);
        if (openingBalance > 0) {
            transactionLogger.log(TransactionType.DEPOSIT, account.getAccountNumber(),
                    openingBalance, "Opening deposit");
        }
        return account;
    }

    @Audit("DEPOSIT")
    public Account deposit(String accountNumber, long amount) throws BankException {
        AuditLogger.logMethodCall(BankService.class, "deposit",
                "account=" + accountNumber + ", amount=" + amount);
        Account account = getRequiredAccount(accountNumber);
        account.deposit(amount);
        recordHistory(accountNumber, "Deposited Rs. " + amount + " | Balance: Rs. " + account.getBalance());
        transactionLogger.log(TransactionType.DEPOSIT, accountNumber, amount, "Cash deposit");
        return account;
    }

    @Audit("WITHDRAW")
    public Account withdraw(String accountNumber, long amount) throws BankException {
        AuditLogger.logMethodCall(BankService.class, "withdraw",
                "account=" + accountNumber + ", amount=" + amount);
        Account account = getRequiredAccount(accountNumber);
        account.withdraw(amount);
        recordHistory(accountNumber, "Withdrew Rs. " + amount + " | Balance: Rs. " + account.getBalance());
        transactionLogger.log(TransactionType.WITHDRAW, accountNumber, amount, "Cash withdrawal");
        return account;
    }

    @Audit("TRANSFER")
    public void transfer(String fromAccountNumber, String toAccountNumber, long amount) throws BankException {
        AuditLogger.logMethodCall(BankService.class, "transfer",
                "from=" + fromAccountNumber + ", to=" + toAccountNumber + ", amount=" + amount);
        Account from = getRequiredAccount(fromAccountNumber);
        Account to = getRequiredAccount(toAccountNumber);
        from.transfer(to, amount);
        recordHistory(fromAccountNumber, "Transferred Rs. " + amount + " to " + toAccountNumber
                + " | Balance: Rs. " + from.getBalance());
        recordHistory(toAccountNumber, "Received Rs. " + amount + " from " + fromAccountNumber
                + " | Balance: Rs. " + to.getBalance());
        transactionLogger.log(TransactionType.TRANSFER, fromAccountNumber + "->" + toAccountNumber,
                amount, "Fund transfer");
    }

    @Audit("STATEMENT")
    public String statement(String accountNumber) throws BankException {
        AuditLogger.logMethodCall(BankService.class, "statement", "account=" + accountNumber);
        Account account = getRequiredAccount(accountNumber);
        List<String> history = accountHistories.getOrDefault(accountNumber, List.of());
        return StatementFormatter.buildStatement(account, history);
    }

    @Audit("LIST_ACCOUNTS")
    public List<Account> listAccounts() {
        AuditLogger.logMethodCall(BankService.class, "listAccounts", "listing all accounts");
        ArrayList<Account> all = accountRepository.findAll();
        all.sort(Comparator.naturalOrder());
        return all;
    }

    public void processBatch(Command[] commands) throws InterruptedException {
        TransactionProcessor processor = new TransactionProcessor();
        for (Account acc : accountRepository.findAll()) {
            processor.registerAccount(acc);
        }
        processor.runProducerConsumerBatch(commands);
        for (Command cmd : commands) {
            if (cmd.type() == TransactionType.DEPOSIT || cmd.type() == TransactionType.WITHDRAW) {
                transactionLogger.log(cmd.type(), cmd.accountNumber(), cmd.amount(), "Batch processed");
            }
        }
        processor.stop();
    }

    public String generateReport() throws IOException {
        return reportGenerator.generateReport();
    }

    public void loadPersistedAccounts() {
        try {
            Account[] loaded = fileStore.load();
            accountRepository.saveAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Notice: Could not load saved accounts (" + e.getMessage() + ")");
        }
    }

    public void saveAccountsOnExit() {
        try {
            List<Account> all = listAccounts();
            fileStore.save(all.toArray(new Account[0]));
        } catch (IOException e) {
            System.err.println("Failed to save accounts: " + e.getMessage());
        }
    }

    public Account getRequiredAccount(String accountNumber) throws AccountNotFoundException {
        Account acc = accountRepository.findById(accountNumber);
        if (acc == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return acc;
    }

    public Repository<Account> getAccountRepository() {
        return accountRepository;
    }

    private void recordHistory(String accountNumber, String message) {
        accountHistories.computeIfAbsent(accountNumber, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(message);
    }
}
