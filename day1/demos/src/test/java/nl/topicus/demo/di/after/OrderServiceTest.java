package nl.topicus.demo.di.after;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import nl.topicus.demo.di.Order;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

    static class FakeMessageSender implements MessageSender {
        final List<String> sent = new ArrayList<>();

        @Override
        public void send(String to, String message) {
            sent.add(to + ": " + message);
        }
    }

    @Test
    void placingAnOrderSendsAThankYouMessage() {
        FakeMessageSender fake = new FakeMessageSender();
        OrderService service = new OrderService(new PaymentService(), fake);

        service.placeOrder(new Order("ada@example.com", 50));

        assertEquals(1, fake.sent.size());
        assertEquals("ada@example.com: Thanks!", fake.sent.get(0));
    }
}
