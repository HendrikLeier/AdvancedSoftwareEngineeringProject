package persisted;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
public class ReportTemplate {

    public ReportTemplate() {
        this.reportTemplateId = UUID.randomUUID();
    }

    @Id
    private final UUID reportTemplateId;

    @Embedded
    private ReportFilter reportFilter;

    public UUID getReportTemplateId() {
        return reportTemplateId;
    }

    public ReportFilter getReportFilter() {
        return reportFilter;
    }

    public void setReportFilter(ReportFilter reportFilter) {
        this.reportFilter = reportFilter;
    }

}
