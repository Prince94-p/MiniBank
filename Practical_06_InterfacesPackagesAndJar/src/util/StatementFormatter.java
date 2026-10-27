package util;

import java.util.List;
import model.Account;
import model.Premium;

/**
 * Practical 4: Uses StringBuilder to construct a multiline account statement.
 */
public class StatementFormatter {
    public static String buildStatement(Account account) {
        return buildStatement(account, List.of());
    }

    public static String buildStatement(Account account, List<String> history) {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("              MINIBANK ACCOUNT STATEMENT          \n");
        sb.append("--------------------------------------------------\n");
        sb.append("Account Number : ").append(account.getAccountNumber()).append("\n");
        sb.append("Owner Name     : ").append(account.getOwnerName()).append("\n");
        sb.append("Account Type   : ").append(account.getClass().getSimpleName()).append("\n");
        sb.append("Premium Status : ").append(account instanceof Premium ? "YES (Premium)" : "Standard").append("\n");
        sb.append("Active Status  : ").append(account.isActive() ? "ACTIVE" : "INACTIVE").append("\n");
        sb.append("Interest Rate  : ").append(String.format("%.2f%%", account.interestRate())).append("\n");
        sb.append("Yearly Interest: Rs. ").append(String.format("%.2f", account.yearlyInterest())).append("\n");
        sb.append("Current Balance: Rs. ").append(account.getBalance()).append("\n");
        if (history != null && !history.isEmpty()) {
            sb.append("--------------------------------------------------\n");
            sb.append("Recent Transactions:\n");
            for (String entry : history) {
                sb.append("  * ").append(entry).append("\n");
            }
        }
        sb.append("--------------------------------------------------");
        return sb.toString();
    }
}
