package model;

/**
 * Practical 1: Record storing the bank's name and branch.
 */
public record BankInfo(String name, String branch) {
    @Override
    public String toString() {
        return "================================================\n"
             + "   " + name + " — " + branch + " Branch\n"
             + "================================================";
    }
}
