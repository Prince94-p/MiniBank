package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Practical 11: Walks the logs folder using Files.newDirectoryStream, reads each file with
 * BufferedReader, computes total deposits/withdrawals, inspects file attributes with
 * Files.readAttributes, and writes a summary report.
 */
public class ReportGenerator {
    private final Path logsDir;
    private final Path reportFile;

    public ReportGenerator() {
        this(Paths.get("logs"), Paths.get("reports", "summary_report.txt"));
    }

    public ReportGenerator(Path logsDir, Path reportFile) {
        this.logsDir = logsDir;
        this.reportFile = reportFile;
    }

    public String generateReport() throws IOException {
        if (!Files.exists(logsDir)) {
            Files.createDirectories(logsDir);
        }
        if (reportFile.getParent() != null) {
            Files.createDirectories(reportFile.getParent());
        }

        long totalDeposits = 0L;
        long totalWithdrawals = 0L;
        int fileCount = 0;
        int transactionCount = 0;
        StringBuilder fileDetails = new StringBuilder();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "*.log")) {
            for (Path logPath : stream) {
                fileCount++;
                BasicFileAttributes attrs = Files.readAttributes(logPath, BasicFileAttributes.class);
                fileDetails.append(String.format("  - %s (Size: %d bytes, Last Modified: %s)%n",
                        logPath.getFileName(), attrs.size(), attrs.lastModifiedTime()));

                try (BufferedReader reader = Files.newBufferedReader(logPath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.isBlank()) continue;
                        String[] parts = line.split("\\|");
                        if (parts.length >= 3) {
                            String type = parts[0].trim();
                            long amount = Long.parseLong(parts[2].trim());
                            if ("DEPOSIT".equalsIgnoreCase(type)) {
                                totalDeposits += amount;
                                transactionCount++;
                            } else if ("WITHDRAW".equalsIgnoreCase(type)) {
                                totalWithdrawals += amount;
                                transactionCount++;
                            } else if ("TRANSFER".equalsIgnoreCase(type)) {
                                transactionCount++;
                            }
                        }
                    }
                }
            }
        }

        long netChange = totalDeposits - totalWithdrawals;
        StringBuilder summary = new StringBuilder();
        summary.append("==================================================\n");
        summary.append("           MINIBANK TRANSACTION SUMMARY           \n");
        summary.append("==================================================\n");
        summary.append("Log Files Scanned   : ").append(fileCount).append("\n");
        if (fileCount > 0) {
            summary.append("File Attributes     :\n").append(fileDetails);
        }
        summary.append("Total Transactions  : ").append(transactionCount).append("\n");
        summary.append("Total Deposits      : Rs. ").append(totalDeposits).append("\n");
        summary.append("Total Withdrawals   : Rs. ").append(totalWithdrawals).append("\n");
        summary.append("Net Change          : Rs. ").append(netChange).append("\n");
        summary.append("==================================================");

        String reportText = summary.toString();
        Files.writeString(reportFile, reportText + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return reportText;
    }
}
