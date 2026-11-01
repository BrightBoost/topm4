package nl.topicus.cdi.example.order;

// TODO Part 2: markeer deze klasse als bean en de constructor als injectiepunt
public class InvoiceService {

    private final PaymentRepository repository;

    public InvoiceService(PaymentRepository repository) {
        this.repository = repository;
    }

    public PaymentRepository getRepository() {
        return repository;
    }
}
