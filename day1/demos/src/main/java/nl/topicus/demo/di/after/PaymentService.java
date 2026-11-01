package nl.topicus.demo.di.after;

import nl.topicus.demo.di.Order;

public class PaymentService {

    public void charge(Order order) {
        System.out.println("[PAYMENT] charged " + order.getTotal());
    }
}
