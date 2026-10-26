import java.util.Scanner;

/**
 * Practical 1 — MiniBank Menu Shell
 * Topics: Java basics, enum, record, switch expression, Scanner.
 */
public class MiniBank {

    public record BankInfo(String name, String branch) {}

    public enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    public static void main(String[] args) {
        BankInfo header = new BankInfo("CHARUSAT MiniBank", "Changa");
        System.out.println("==========================================");
        System.out.println("  " + header.name() + " (" + header.branch() + " Branch)");
        System.out.println("  Record: " + header);
        System.out.println("==========================================");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- MiniBank Menu ---");
            MenuOption[] options = MenuOption.values();
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }
            System.out.print("Select an option (1-" + options.length + "): ");

            if (!scanner.hasNextInt()) {
                break;
            }
            int choice = scanner.nextInt();
            if (choice < 1 || choice > options.length) {
                System.out.println("Invalid choice. Please enter 1 to " + options.length + ".");
                continue;
            }

            MenuOption selected = options[choice - 1];
            String response = switch (selected) {
                case OPEN_ACCOUNT -> "[Placeholder] Open Account selected.";
                case DEPOSIT      -> "[Placeholder] Deposit selected.";
                case WITHDRAW     -> "[Placeholder] Withdraw selected.";
                case TRANSFER     -> "[Placeholder] Transfer selected.";
                case EXIT -> {
                    running = false;
                    yield "Exiting MiniBank. Goodbye!";
                }
            };
            System.out.println(response);
        }
        scanner.close();
    }
}
