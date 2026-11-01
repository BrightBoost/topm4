package nl.topicus.demo.di.before;

import nl.topicus.demo.di.Order;

public class OrderService {

    private final PaymentService payment = new PaymentService();
    private final EmailSender email = new EmailSender();

    public void placeOrder(Order order) {
        payment.charge(order);
        email.send(order.getCustomerEmail(), "Thanks!");
    }
}
