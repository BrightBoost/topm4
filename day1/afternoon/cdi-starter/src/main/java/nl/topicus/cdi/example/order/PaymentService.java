package nl.topicus.cdi.example.order;

// TODO Part 2: markeer deze klasse als bean en de constructor als injectiepunt
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public PaymentRepository getRepository() {
        return repository;
    }
}
