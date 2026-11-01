package nl.topicus.demo.di.after;

import nl.topicus.demo.di.Order;

public class OrderService {

    private final PaymentService payment;
    private final MessageSender sender;

    public OrderService(PaymentService payment, MessageSender sender) {
        this.payment = payment;
        this.sender = sender;
    }

    public void placeOrder(Order order) {
        payment.charge(order);
        sender.send(order.getCustomerEmail(), "Thanks!");
    }
}
