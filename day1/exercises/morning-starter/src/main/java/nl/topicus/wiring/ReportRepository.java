package nl.topicus.wiring;

import java.util.List;

public class ReportRepository {

    private final DataSourceProvider dataSource = new DataSourceProvider();

    public List<String> findRows() {
        dataSource.connect();
        return List.of("Ada;36", "Linus;55", "Grace;85");
    }
}
