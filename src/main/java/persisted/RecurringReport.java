package persisted;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
public class RecurringReport {

    public RecurringReport() {
        this.recurringReportId = UUID.randomUUID();
    }

    @Id
    private final UUID recurringReportId;

    @ManyToOne
    private ReportTemplate reportTemplate;

    @OneToOne
    private RecurrentRule rule;

    private String name;

    private String dateVariable;

    public UUID getRecurringReportId() {
        return recurringReportId;
    }

    public ReportTemplate getReportTemplate() {
        return reportTemplate;
    }

    public void setReportTemplate(ReportTemplate reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    public RecurrentRule getRule() {
        return rule;
    }

    public void setRule(RecurrentRule rule) {
        this.rule = rule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateVariable() {
        return dateVariable;
    }

    public void setDateVariable(String dateVariable) {
        this.dateVariable = dateVariable;
    }
}
