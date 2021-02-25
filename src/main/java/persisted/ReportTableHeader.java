package persisted;

import javax.persistence.*;

import java.util.*;

@Embeddable
public class ReportTableHeader {

    /*
    public ReportTableHeader() {
        this.reportTableId = UUID.randomUUID();
    }

    @Id
    private final UUID reportTableId;*/

    @ElementCollection
    @CollectionTable(name="report_table_header", joinColumns=@JoinColumn(name="report_table_id"))
    private List<Pair> columns;

    public List<Pair> getColumns() {
        return columns;
    }

    public void setColumns(List<Pair> columns) {
        this.columns = columns;
    }
    /*
    public UUID getReportTableId() {
        return reportTableId;
    }*/
}
