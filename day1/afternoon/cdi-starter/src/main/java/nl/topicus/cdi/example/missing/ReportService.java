package nl.topicus.cdi.example.missing;

// TODO Part 2: markeer deze klasse als bean en de constructor als injectiepunt
public class ReportService {

    private final AuditLog auditLog;

    public ReportService(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }
}
