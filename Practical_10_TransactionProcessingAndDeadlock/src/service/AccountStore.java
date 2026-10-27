package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import model.Account;

/**
 * Practical 12: AccountStore backed by HashMap<String, Account> and ArrayList<String> history,
 * with Comparator-based sorting by balance.
 */
public class AccountStore {
    private final HashMap<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    public void add(Account account) {
        if (account != null) {
            accounts.put(account.getAccountNumber(), account);
            recordTransaction("Added account " + account.getAccountNumber()
                    + " (" + account.getOwnerName() + ") with balance Rs. " + account.getBalance());
        }
    }

    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort(null); // Natural ordering via Account.compareTo (by accountNumber)
        return list;
    }

    public void recordTransaction(String entry) {
        transactionHistory.add(entry);
    }

    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    /**
     * Practical 12: Returns accounts sorted by balance in descending order using a Comparator.
     */
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        Comparator<Account> byBalanceDesc = Comparator.comparingLong(Account::getBalance).reversed();
        list.sort(byBalanceDesc);
        return list;
    }
}
