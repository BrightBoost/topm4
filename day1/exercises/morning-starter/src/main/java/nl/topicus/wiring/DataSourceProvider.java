package nl.topicus.wiring;

public class DataSourceProvider {

    public String connect() {
        String url = System.getProperty("report.db.url");
        if (url == null) {
            throw new IllegalStateException("No database configured: set -Dreport.db.url");
        }
        return url;
    }
}
