package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.TransactionType;

/**
 * Practical 11: Writes transaction records to a log file using NIO Files with CREATE and APPEND.
 */
public class TransactionLogger {
    private final Path logFile;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TransactionLogger() {
        this(Paths.get("logs", "transactions.log"));
    }

    public TransactionLogger(Path logFile) {
        this.logFile = logFile;
    }

    public synchronized void log(TransactionType type, String accountNumber, long amount, String note) {
        try {
            if (logFile.getParent() != null) {
                Files.createDirectories(logFile.getParent());
            }
            String line = String.format("%s|%s|%d|%s|%s%n",
                    type.name(), accountNumber, amount, LocalDateTime.now().format(FMT), note);
            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write transaction log: " + e.getMessage());
        }
    }

    public Path getLogFile() {
        return logFile;
    }
}
