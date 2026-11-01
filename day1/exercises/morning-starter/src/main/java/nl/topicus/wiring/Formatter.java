package nl.topicus.wiring;

import java.util.List;

public class Formatter {

    public String format(List<String> rows) {
        StringBuilder report = new StringBuilder("NAME;AGE\n");
        for (String row : rows) {
            report.append(row).append('\n');
        }
        return report.toString();
    }
}
