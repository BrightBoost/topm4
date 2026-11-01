package nl.topicus.cdi;

/**
 * Basisklasse voor alle fouten van de container. De tests verwachten deze klasse,
 * je mag er eigen subklassen van maken (bijvoorbeeld voor ontbrekende of circulaire dependencies).
 */
public class ContainerException extends RuntimeException {

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }
}
