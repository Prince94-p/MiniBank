package service;

import exception.InvalidAmountException;
import model.Account;

/**
 * Practical 9: Runnable worker that performs repeated deposits on a shared Account.
 */
public class AccountWorker implements Runnable {
    private final Account account;
    private final int times;
    private final long amount;
    private final boolean verbose;

    public AccountWorker(Account account, int times, long amount) {
        this(account, times, amount, true);
    }

    public AccountWorker(Account account, int times, long amount, boolean verbose) {
        this.account = account;
        this.times = times;
        this.amount = amount;
        this.verbose = verbose;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        if (verbose) {
            System.out.println("[" + threadName + "] Started -> performing " + times + " deposits of Rs. " + amount);
        }
        for (int i = 0; i < times; i++) {
            try {
                account.deposit(amount);
            } catch (InvalidAmountException e) {
                System.err.println("[" + threadName + "] Invalid deposit amount: " + e.getMessage());
            }
        }
        if (verbose) {
            System.out.println("[" + threadName + "] Completed.");
        }
    }
}
