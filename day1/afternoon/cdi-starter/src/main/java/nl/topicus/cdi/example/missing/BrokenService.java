package nl.topicus.cdi.example.missing;

// Deze constructor gooit altijd een exception
public class BrokenService {

    public BrokenService() {
        throw new IllegalStateException("boom");
    }
}
