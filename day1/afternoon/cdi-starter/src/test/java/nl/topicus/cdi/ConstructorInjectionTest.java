package nl.topicus.cdi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import nl.topicus.cdi.example.order.InvoiceService;
import nl.topicus.cdi.example.order.OrderService;
import nl.topicus.cdi.example.order.PaymentRepository;
import nl.topicus.cdi.example.order.PaymentService;
import org.junit.jupiter.api.Test;

class ConstructorInjectionTest {

    private Container containerWithOrderBeans() {
        Container container = new Container();
        container.registerClass(OrderService.class);
        container.registerClass(PaymentService.class);
        container.registerClass(InvoiceService.class);
        container.registerClass(PaymentRepository.class);
        return container;
    }

    @Test
    void wholeChainIsInjected() {
        Container container = containerWithOrderBeans();

        OrderService orderService = container.getBean(OrderService.class);

        assertNotNull(orderService);
        assertNotNull(orderService.getPaymentService());
        assertNotNull(orderService.getInvoiceService());
        assertNotNull(orderService.getPaymentService().getRepository());
    }

    @Test
    void sameClassTwiceReturnsSameInstance() {
        Container container = containerWithOrderBeans();

        assertSame(container.getBean(OrderService.class), container.getBean(OrderService.class));
    }

    @Test
    void sharedDependencyExistsOnce() {
        Container container = containerWithOrderBeans();

        OrderService orderService = container.getBean(OrderService.class);

        assertSame(
                orderService.getPaymentService().getRepository(),
                orderService.getInvoiceService().getRepository());
    }

    @Test
    void classWithOnlyNoArgConstructorWorksWithoutInject() {
        Container container = new Container();
        container.registerClass(PaymentRepository.class);

        assertNotNull(container.getBean(PaymentRepository.class));
    }
}
