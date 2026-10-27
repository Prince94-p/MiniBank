package service;

import exception.BankException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.Account;
import model.Command;
import model.TransactionType;

/**
 * Practical 10 & 12: TransactionProcessor using a fixed thread pool (Executors.newFixedThreadPool(4)),
 * ConcurrentHashMap for shared thread-safe accounts, a producer-consumer shared array buffer with
 * wait()/notify(), and deadlock demonstration + consistent lock-ordering fix.
 */
public class TransactionProcessor {
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> sharedAccounts;

    public TransactionProcessor() {
        this.executor = Executors.newFixedThreadPool(4);
        this.sharedAccounts = new ConcurrentHashMap<>();
    }

    public void registerAccount(Account account) {
        if (account != null) {
            sharedAccounts.put(account.getAccountNumber(), account);
        }
    }

    public Account getSharedAccount(String accountNumber) {
        return sharedAccounts.get(accountNumber);
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Practical 10: Shared array buffer using wait() and notify() inside synchronized blocks
     * for Producer-Consumer coordination.
     */
    public static class SharedTransactionBuffer {
        private final Command[] buffer;
        private int head = 0;
        private int tail = 0;
        private int count = 0;

        public SharedTransactionBuffer(int capacity) {
            this.buffer = new Command[capacity];
        }

        public synchronized void produce(Command cmd) throws InterruptedException {
            while (count == buffer.length) {
                wait();
            }
            buffer[tail] = cmd;
            tail = (tail + 1) % buffer.length;
            count++;
            notifyAll();
        }

        public synchronized Command consume() throws InterruptedException {
            while (count == 0) {
                wait();
            }
            Command cmd = buffer[head];
            head = (head + 1) % buffer.length;
            count--;
            notifyAll();
            return cmd;
        }
    }

    /**
     * Practical 10: Runs a Producer-Consumer batch over the shared array buffer.
     */
    public void runProducerConsumerBatch(Command[] commands) throws InterruptedException {
        SharedTransactionBuffer buffer = new SharedTransactionBuffer(3);

        Thread producer = new Thread(() -> {
            try {
                for (Command cmd : commands) {
                    buffer.produce(cmd);
                    System.out.println("[Producer] Queued: " + cmd.type() + " "
                            + cmd.accountNumber() + " Rs." + cmd.amount());
                }
                // Poison pill to signal end of stream
                buffer.produce(new Command(TransactionType.DEPOSIT, "END", 0L));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Tx-Producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    Command cmd = buffer.consume();
                    if ("END".equals(cmd.accountNumber())) {
                        break;
                    }
                    Account acc = sharedAccounts.get(cmd.accountNumber());
                    if (acc != null) {
                        try {
                            if (cmd.type() == TransactionType.DEPOSIT) {
                                acc.deposit(cmd.amount());
                            } else if (cmd.type() == TransactionType.WITHDRAW) {
                                acc.withdraw(cmd.amount());
                            }
                            System.out.println("[Consumer] Processed: " + cmd.type() + " on "
                                    + acc.getAccountNumber() + " -> Balance: Rs." + acc.getBalance());
                        } catch (BankException e) {
                            System.out.println("[Consumer] Failed: " + e.getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Tx-Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

    /**
     * Practical 10: Demonstrates how acquiring locks in opposite order (A->B vs B->A)
     * causes a deadlock (detected via ThreadMXBean), and how consistent lock ordering
     * (smaller accountNumber first) prevents deadlock.
     */
    public static void demonstrateDeadlockAndFix(Account accA, Account accB, long amount) throws Exception {
        final Object lockA = new Object();
        final Object lockB = new Object();
        CountDownLatch bothAcquiredFirstLock = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                bothAcquiredFirstLock.countDown();
                try {
                    bothAcquiredFirstLock.await(200, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignored) {
                }
                // Try acquiring lockB while t2 holds lockB
                synchronized (lockB) {
                    System.out.println("T1 acquired both locks");
                }
            }
        }, "Deadlock-Thread-A-to-B");

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                bothAcquiredFirstLock.countDown();
                try {
                    bothAcquiredFirstLock.await(200, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignored) {
                }
                // Try acquiring lockA while t1 holds lockA
                synchronized (lockA) {
                    System.out.println("T2 acquired both locks");
                }
            }
        }, "Deadlock-Thread-B-to-A");

        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        Thread.sleep(150);

        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] deadlockedIds = bean.findDeadlockedThreads();
        if (deadlockedIds != null && deadlockedIds.length >= 2) {
            System.out.println("[Deadlock Detector] Opposite lock ordering (A->B and B->A) caused DEADLOCK between "
                    + deadlockedIds.length + " daemon threads!");
        }

        // Now perform two simultaneous transfers using consistent lock ordering (smaller accountNumber first)
        System.out.println("[Deadlock Fix] Running concurrent transfers A->B and B->A locking smaller accountNumber first...");
        Thread safe1 = new Thread(() -> {
            try {
                accA.transfer(accB, amount);
            } catch (BankException e) {
                System.err.println(e.getMessage());
            }
        }, "SafeTransfer-A-to-B");

        Thread safe2 = new Thread(() -> {
            try {
                accB.transfer(accA, amount);
            } catch (BankException e) {
                System.err.println(e.getMessage());
            }
        }, "SafeTransfer-B-to-A");

        safe1.start();
        safe2.start();
        safe1.join();
        safe2.join();
        System.out.println("[Deadlock Fix] Both transfers completed safely without deadlock! "
                + accA.getAccountNumber() + "=" + accA.getBalance() + ", "
                + accB.getAccountNumber() + "=" + accB.getBalance());
    }
}
