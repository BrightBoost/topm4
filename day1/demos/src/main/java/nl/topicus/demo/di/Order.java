package nl.topicus.demo.di;

public class Order {

    private final String customerEmail;
    private final int total;

    public Order(String customerEmail, int total) {
        this.customerEmail = customerEmail;
        this.total = total;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public int getTotal() {
        return total;
    }
}
