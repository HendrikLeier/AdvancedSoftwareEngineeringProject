package persisted;

import javax.persistence.*;

import java.util.*;

@Embeddable
public class ReportFilter {

    @Column(nullable = false)
    private String origQuery;

    @ElementCollection
    @CollectionTable(name="report_filter_variables", joinColumns=@JoinColumn(name="report_filter_id"))
    @Column(name="variables")
    private List<String> variables;

    public String getOrigQuery() {
        return origQuery;
    }

    public void setOrigQuery(String origQuery) {
        this.origQuery = origQuery;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }
}
