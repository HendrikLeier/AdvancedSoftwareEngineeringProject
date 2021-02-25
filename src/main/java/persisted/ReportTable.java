package persisted;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
public class ReportTable {

    public ReportTable() {
        this.reportTableId = UUID.randomUUID();
    }

    @Id
    private final UUID reportTableId;

    @OneToMany(mappedBy = "reportTable", fetch = FetchType.LAZY)
    private List<ReportTableRow> reportTableRows;

    private ReportTableHeader header;

    public UUID getReportTableId() {
        return reportTableId;
    }

    public List<ReportTableRow> getReportTableRows() {
        return reportTableRows;
    }

    public void setReportTableRows(List<ReportTableRow> reportTableRows) {
        this.reportTableRows = reportTableRows;
    }

    public ReportTableHeader getHeader() {
        return header;
    }

    public void setHeader(ReportTableHeader header) {
        this.header = header;
    }
}
