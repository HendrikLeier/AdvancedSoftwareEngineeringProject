package persisted;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class ReportResult {

    public ReportResult() {
        this.reportResultId = UUID.randomUUID();
    }

    @Id
    private final UUID reportResultId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @OneToOne(optional = false)
    private ReportTable reportTable;

    @ElementCollection
    @CollectionTable(name="report_variable_values", joinColumns=@JoinColumn(name="report_result_id"))
    private List<Pair> variableValues;

    public UUID getReportResultId() {
        return reportResultId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ReportTable getReportTable() {
        return reportTable;
    }

    public void setReportTable(ReportTable reportTable) {
        this.reportTable = reportTable;
    }

    public List<Pair> getVariableValues() {
        return variableValues;
    }

    public void setVariableValues(List<Pair> variableValues) {
        this.variableValues = variableValues;
    }
}
