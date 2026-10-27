package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.Account;

/**
 * Practical 11: Saves and loads Account[] arrays using ObjectOutputStream and ObjectInputStream.
 */
public class AccountFileStore {
    private final String filePath;

    public AccountFileStore() {
        this("accounts.dat");
    }

    public AccountFileStore(String filePath) {
        this.filePath = filePath;
    }

    public void save(Account[] accounts) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(accounts);
        }
    }

    public Account[] load() throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new Account[0];
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Account[] loaded = (Account[]) ois.readObject();
            for (Account acc : loaded) {
                if (acc != null) {
                    Account.syncCounterWith(acc.getAccountNumber());
                }
            }
            return loaded;
        }
    }
}
