package nl.topicus.cdi.example.order;

// TODO Part 2: markeer deze klasse als bean en de constructor als injectiepunt
public class OrderService {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    public OrderService(PaymentService paymentService, InvoiceService invoiceService) {
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public InvoiceService getInvoiceService() {
        return invoiceService;
    }
}
