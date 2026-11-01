package nl.topicus.demo.di.after;

import nl.topicus.demo.di.Order;

public class AfterMain {

    public static void main(String[] args) {
        OrderService service = new OrderService(new PaymentService(), new SmtpSender());
        service.placeOrder(new Order("ada@example.com", 50));
    }
}
