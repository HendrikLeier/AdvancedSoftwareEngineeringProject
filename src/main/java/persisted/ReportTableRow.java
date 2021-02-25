package persisted;

import javax.persistence.*;
import java.util.UUID;

import java.util.*;

@Entity
public class ReportTableRow {

    public ReportTableRow() {
        this.reportTableId = UUID.randomUUID();
    }

    @Id
    private final UUID reportTableId;

    @ElementCollection
    @CollectionTable(name="report_table_row_values", joinColumns=@JoinColumn(name="report_table_row_id"))
    private List<String> values;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReportTable reportTable;

    public UUID getReportTableId() {
        return reportTableId;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
