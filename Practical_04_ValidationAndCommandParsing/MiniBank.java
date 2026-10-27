public class MiniBank {
    public static void main(String[] args) {
        System.out.println("isValidMobile(\"9876543210\"): " + Validator.isValidMobile("9876543210"));
        System.out.println("isValidMobile(\"12345\")     : " + Validator.isValidMobile("12345"));
        System.out.println("isValidEmail(\"riya@bank.in\"): " + Validator.isValidEmail("riya@bank.in"));
        System.out.println("isValidEmail(\"invalid\")     : " + Validator.isValidEmail("invalid"));
        System.out.println("isValidPan(\"ABCDE1234F\")   : " + Validator.isValidPan("ABCDE1234F"));
        System.out.println("isValidPan(\"123\")          : " + Validator.isValidPan("123"));
        System.out.println("isValidIfsc(\"SBIN0001234\") : " + Validator.isValidIfsc("SBIN0001234"));
        System.out.println("isValidIfsc(\"BADIFSC\")     : " + Validator.isValidIfsc("BADIFSC"));

        Command cmd = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.println("Parsed Command: type=" + cmd.type()
                + ", account=" + cmd.accountNumber() + ", amount=" + cmd.amount());

        Account acc = new Account("Riya", 4000L);
        System.out.println(StatementFormatter.buildStatement(acc));
    }
}
