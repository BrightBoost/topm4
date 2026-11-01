package nl.topicus.cdi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.topicus.cdi.example.cycle.ChainA;
import nl.topicus.cdi.example.cycle.ChainB;
import nl.topicus.cdi.example.cycle.ChainC;
import nl.topicus.cdi.example.cycle.CycleA;
import nl.topicus.cdi.example.cycle.CycleB;
import nl.topicus.cdi.example.order.PaymentRepository;
import org.junit.jupiter.api.Test;

class CircularDependencyTest {

    @Test
    void directCycleIsDetected() {
        Container container = new Container();
        container.registerClass(CycleA.class);
        container.registerClass(CycleB.class);

        ContainerException e = assertThrows(ContainerException.class, () -> container.getBean(CycleA.class));

        assertTrue(e.getMessage().contains("CycleA -> CycleB -> CycleA"), "message was: " + e.getMessage());
    }

    @Test
    void longerCycleIsDetected() {
        Container container = new Container();
        container.registerClass(ChainA.class);
        container.registerClass(ChainB.class);
        container.registerClass(ChainC.class);

        ContainerException e = assertThrows(ContainerException.class, () -> container.getBean(ChainA.class));

        assertTrue(e.getMessage().contains("ChainA -> ChainB -> ChainC -> ChainA"), "message was: " + e.getMessage());
    }

    @Test
    void containerStaysUsableAfterFailedAttempt() {
        Container container = new Container();
        container.registerClass(CycleA.class);
        container.registerClass(CycleB.class);
        container.registerClass(PaymentRepository.class);

        assertThrows(ContainerException.class, () -> container.getBean(CycleA.class));

        assertNotNull(container.getBean(PaymentRepository.class));
        // opnieuw proberen geeft dezelfde duidelijke fout, geen half-aangemaakte bean
        assertThrows(ContainerException.class, () -> container.getBean(CycleA.class));
    }
}
