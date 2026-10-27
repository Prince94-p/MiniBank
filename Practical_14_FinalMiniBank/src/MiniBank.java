import exception.BankException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import model.Account;
import model.BankInfo;
import model.Command;
import model.MenuOption;
import service.BankService;
import util.CommandParser;

/**
 * Practical 1 & 14: Main entry-point class for MiniBank connecting the interactive
 * CLI menu to BankService, file persistence, batch processing, and reporting.
 *
 * Run interactively : java -jar MiniBank.jar
 * Run all 14 demos  : java -jar MiniBank.jar --demo
 */
public class MiniBank {

    public static void main(String[] args) {
        if (args.length > 0 && ("--demo".equalsIgnoreCase(args[0]) || "demo".equalsIgnoreCase(args[0]))) {
            PracticalDemos.runAllDemos();
            return;
        }

        BankInfo bankInfo = new BankInfo("CHARUSAT MiniBank", "Changa");
        System.out.println(bankInfo);

        BankService bankService = new BankService();
        bankService.loadPersistedAccounts();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Enter your choice (1-" + MenuOption.values().length + "): ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }

                int choiceNum;
                try {
                    choiceNum = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and "
                            + MenuOption.values().length + ".");
                    continue;
                }

                MenuOption[] options = MenuOption.values();
                if (choiceNum < 1 || choiceNum > options.length) {
                    System.out.println("Invalid option. Please choose between 1 and " + options.length + ".");
                    continue;
                }

                MenuOption selected = options[choiceNum - 1];
                String statusMessage = switch (selected) {
                    case OPEN_ACCOUNT    -> handleOpenAccount(scanner, bankService);
                    case DEPOSIT         -> handleDeposit(scanner, bankService);
                    case WITHDRAW        -> handleWithdraw(scanner, bankService);
                    case TRANSFER        -> handleTransfer(scanner, bankService);
                    case STATEMENT       -> handleStatement(scanner, bankService);
                    case LIST_ACCOUNTS   -> handleListAccounts(bankService);
                    case GENERATE_REPORT -> handleGenerateReport(bankService);
                    case PROCESS_BATCH   -> handleProcessBatch(scanner, bankService);
                    case RUN_ALL_DEMOS   -> {
                        PracticalDemos.runAllDemos();
                        yield "Completed all 14 Practical demonstrations.";
                    }
                    case EXIT -> {
                        bankService.saveAccountsOnExit();
                        running = false;
                        yield "Accounts saved to accounts.dat. Thank you for banking with MiniBank. Goodbye!";
                    }
                };
                System.out.println(statusMessage);
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n---------------- MINIBANK MENU ----------------");
        MenuOption[] options = MenuOption.values();
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%2d. %s%n", (i + 1), formatOptionLabel(options[i]));
        }
        System.out.println("-----------------------------------------------");
    }

    private static String formatOptionLabel(MenuOption option) {
        return switch (option) {
            case OPEN_ACCOUNT    -> "OPEN_ACCOUNT    (Open Savings / Current / FD Account)";
            case DEPOSIT         -> "DEPOSIT         (Deposit Funds)";
            case WITHDRAW        -> "WITHDRAW        (Withdraw Funds)";
            case TRANSFER        -> "TRANSFER        (Transfer Between Accounts)";
            case STATEMENT       -> "STATEMENT       (View Account Statement)";
            case LIST_ACCOUNTS   -> "LIST_ACCOUNTS   (List All Saved Accounts)";
            case GENERATE_REPORT -> "GENERATE_REPORT (Generate NIO Transaction Log Report)";
            case PROCESS_BATCH   -> "PROCESS_BATCH   (Process Multithreaded Command Batch)";
            case RUN_ALL_DEMOS   -> "RUN_ALL_DEMOS   (Run Practicals 1-14 Expected Result Demos)";
            case EXIT            -> "EXIT            (Save & Exit)";
        };
    }

    private static String handleOpenAccount(Scanner scanner, BankService service) {
        try {
            System.out.print("Account Type (SAVINGS / CURRENT / FD): ");
            String type = scanner.nextLine().trim();
            System.out.print("Owner Name: ");
            String ownerName = scanner.nextLine().trim();
            System.out.print("Mobile Number (10 digits): ");
            String mobile = scanner.nextLine().trim();
            System.out.print("Opening Balance (Rs.): ");
            long openingBalance = Long.parseLong(scanner.nextLine().trim());

            Account created = service.openAccount(type, ownerName, mobile, openingBalance);
            return "Account opened successfully: " + created;
        } catch (BankException | NumberFormatException e) {
            return "Failed to open account: " + e.getMessage();
        }
    }

    private static String handleDeposit(Scanner scanner, BankService service) {
        try {
            System.out.print("Account Number (e.g., AC0001): ");
            String accNo = scanner.nextLine().trim();
            System.out.print("Deposit Amount (Rs.): ");
            long amount = Long.parseLong(scanner.nextLine().trim());

            Account updated = service.deposit(accNo, amount);
            return "Deposit successful -> " + updated;
        } catch (BankException | NumberFormatException e) {
            return "Deposit failed: " + e.getMessage();
        }
    }

    private static String handleWithdraw(Scanner scanner, BankService service) {
        try {
            System.out.print("Account Number (e.g., AC0001): ");
            String accNo = scanner.nextLine().trim();
            System.out.print("Withdrawal Amount (Rs.): ");
            long amount = Long.parseLong(scanner.nextLine().trim());

            Account updated = service.withdraw(accNo, amount);
            return "Withdrawal successful -> " + updated;
        } catch (BankException | NumberFormatException e) {
            return e.getMessage();
        }
    }

    private static String handleTransfer(Scanner scanner, BankService service) {
        try {
            System.out.print("From Account Number: ");
            String fromAcc = scanner.nextLine().trim();
            System.out.print("To Account Number: ");
            String toAcc = scanner.nextLine().trim();
            System.out.print("Transfer Amount (Rs.): ");
            long amount = Long.parseLong(scanner.nextLine().trim());

            service.transfer(fromAcc, toAcc, amount);
            return "Transferred Rs. " + amount + " from " + fromAcc + " to " + toAcc + " successfully.";
        } catch (BankException | NumberFormatException e) {
            return "Transfer failed: " + e.getMessage();
        }
    }

    private static String handleStatement(Scanner scanner, BankService service) {
        try {
            System.out.print("Account Number: ");
            String accNo = scanner.nextLine().trim();
            return service.statement(accNo);
        } catch (BankException e) {
            return "Could not build statement: " + e.getMessage();
        }
    }

    private static String handleListAccounts(BankService service) {
        List<Account> accounts = service.listAccounts();
        if (accounts.isEmpty()) {
            return "No accounts found.";
        }
        StringBuilder sb = new StringBuilder("Registered Accounts (" + accounts.size() + "):\n");
        for (Account acc : accounts) {
            sb.append("  ").append(acc).append(" [").append(acc.getClass().getSimpleName()).append("]\n");
        }
        return sb.toString().trim();
    }

    private static String handleGenerateReport(BankService service) {
        try {
            return service.generateReport();
        } catch (IOException e) {
            return "Failed to generate report: " + e.getMessage();
        }
    }

    private static String handleProcessBatch(Scanner scanner, BankService service) {
        try {
            System.out.print("Enter semicolon-separated commands (e.g., DEPOSIT AC0001 500; WITHDRAW AC0001 200): ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                return "No commands entered.";
            }
            String[] parts = raw.split(";");
            Command[] commands = new Command[parts.length];
            for (int i = 0; i < parts.length; i++) {
                commands[i] = CommandParser.parse(parts[i].trim());
            }
            service.processBatch(commands);
            return "Batch of " + commands.length + " commands processed safely.";
        } catch (Exception e) {
            return "Batch processing error: " + e.getMessage();
        }
    }
}
