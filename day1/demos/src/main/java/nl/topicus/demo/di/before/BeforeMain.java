package nl.topicus.demo.di.before;

import nl.topicus.demo.di.Order;

public class BeforeMain {

    public static void main(String[] args) {
        OrderService service = new OrderService();
        service.placeOrder(new Order("ada@example.com", 50));
    }
}
