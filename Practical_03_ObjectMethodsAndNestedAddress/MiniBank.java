public class MiniBank {
    public static void main(String[] args) throws CloneNotSupportedException {
        Account a1 = new Account("Riya", 4000L);
        Account a2 = new Account("Aarav", 7000L);

        System.out.println(a1);
        System.out.println("a1.equals(a1): " + a1.equals(a1));
        System.out.println("a1.equals(a2): " + a1.equals(a2));
        System.out.println("a1 instanceof Account: " + (a1 instanceof Account));

        Customer.Address addr = new Customer.Address("Changa Road", "Anand", "388421");
        Customer c1 = new Customer("Riya", "riya@charusat.edu.in", "9876543210", addr);
        Customer c1Clone = c1.clone();
        System.out.println("Original: " + c1);
        System.out.println("Cloned  : " + c1Clone + " (distinct object=" + (c1 != c1Clone) + ")");
    }
}
