package nl.topicus.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.topicus.cdi.example.missing.BrokenService;
import nl.topicus.cdi.example.missing.ReportService;
import org.junit.jupiter.api.Test;

class MissingDependencyTest {

    @Test
    void missingDependencyThrowsContainerException() {
        Container container = new Container();
        container.registerClass(ReportService.class);

        ContainerException e = assertThrows(ContainerException.class, () -> container.getBean(ReportService.class));

        assertTrue(e.getMessage().contains("AuditLog"), "message should name the missing type: " + e.getMessage());
        assertTrue(e.getMessage().contains("ReportService"), "message should name the requesting class: " + e.getMessage());
    }

    @Test
    void exceptionInConstructorKeepsOriginalCause() {
        Container container = new Container();
        container.registerClass(BrokenService.class);

        ContainerException e = assertThrows(ContainerException.class, () -> container.getBean(BrokenService.class));

        Throwable cause = e.getCause();
        assertNotNull(cause, "original exception should be the cause");
        assertInstanceOf(IllegalStateException.class, cause);
        assertEquals("boom", cause.getMessage());
    }

    @Test
    void reflectionWrapperIsNotShownToTheUser() {
        Container container = new Container();
        container.registerClass(BrokenService.class);

        ContainerException e = assertThrows(ContainerException.class, () -> container.getBean(BrokenService.class));

        assertFalse(String.valueOf(e.getMessage()).contains("InvocationTargetException"));
        assertFalse(e.getCause() instanceof java.lang.reflect.InvocationTargetException);
    }
}
