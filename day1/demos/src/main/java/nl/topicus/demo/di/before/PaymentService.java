package nl.topicus.demo.di.before;

import nl.topicus.demo.di.Order;

public class PaymentService {

    public void charge(Order order) {
        System.out.println("[PAYMENT] charged " + order.getTotal());
    }
}
