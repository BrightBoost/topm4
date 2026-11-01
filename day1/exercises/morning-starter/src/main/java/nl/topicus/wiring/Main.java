package nl.topicus.wiring;

public class Main {

    public static void main(String[] args) {
        System.setProperty("report.db.url", "jdbc:h2:mem:reports");

        ReportService service = new ReportService();
        System.out.println(service.buildReport());
    }
}
