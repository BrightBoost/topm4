package nl.topicus.cdi;

/**
 * De mini-CDI container. De publieke methodes staan vast omdat de tests ze gebruiken.
 * Alles daarachter bepaal je zelf.
 */
public class Container {

    /** Part 1: registreer een kant-en-klaar object onder zijn type. */
    public <T> void register(Class<T> type, T instance) {
        throw new UnsupportedOperationException("TODO Part 1");
    }

    /** Part 3: registreer een klasse die de container zelf mag aanmaken. */
    public void registerClass(Class<?> type) {
        throw new UnsupportedOperationException("TODO Part 3");
    }

    /** Geeft de bean voor dit type terug, en maakt hem (met dependencies) zo nodig aan. */
    public <T> T getBean(Class<T> type) {
        throw new UnsupportedOperationException("TODO Part 1 en 3");
    }
}
