public class Customer implements Cloneable {
    private static int counter = 100;

    private final String customerId;
    private String name;
    private String email;
    private String mobile;
    private Address address;

    public static class Address implements Cloneable {
        private String line;
        private String city;
        private String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() { return line; }
        public String getCity() { return city; }
        public String getPincode() { return pincode; }

        @Override
        public Address clone() throws CloneNotSupportedException {
            return (Address) super.clone();
        }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }

    private static String generateCustomerId() {
        return "CUST" + (++counter);
    }

    public Customer(String name, String email, String mobile, Address address) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public Address getAddress() { return address; }

    @Override
    public Customer clone() throws CloneNotSupportedException {
        Customer copy = (Customer) super.clone();
        if (this.address != null) {
            copy.address = this.address.clone();
        }
        return copy;
    }

    @Override
    public String toString() {
        return customerId + " | " + name + " | " + address;
    }
}
