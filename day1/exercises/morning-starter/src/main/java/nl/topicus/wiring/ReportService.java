package nl.topicus.wiring;

public class ReportService {

    private final ReportRepository repository = new ReportRepository();
    private final Formatter formatter = new Formatter();

    public String buildReport() {
        return formatter.format(repository.findRows());
    }
}
