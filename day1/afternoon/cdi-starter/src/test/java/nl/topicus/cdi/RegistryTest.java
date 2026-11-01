package nl.topicus.cdi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.topicus.cdi.example.order.PaymentRepository;
import org.junit.jupiter.api.Test;

class RegistryTest {

    @Test
    void registeredObjectCanBeRetrieved() {
        Container container = new Container();
        PaymentRepository repository = new PaymentRepository();
        container.register(PaymentRepository.class, repository);

        PaymentRepository found = container.getBean(PaymentRepository.class);

        assertNotNull(found);
        assertSame(repository, found);
    }

    @Test
    void sameTypeReturnsSameObject() {
        Container container = new Container();
        container.register(PaymentRepository.class, new PaymentRepository());

        assertSame(container.getBean(PaymentRepository.class), container.getBean(PaymentRepository.class));
    }

    @Test
    void unknownTypeThrowsContainerException() {
        Container container = new Container();

        assertThrows(ContainerException.class, () -> container.getBean(PaymentRepository.class));
    }
}
