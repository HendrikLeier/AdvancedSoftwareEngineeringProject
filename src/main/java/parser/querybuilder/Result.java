package parser.querybuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;
import java.util.*;

public class Result {

    private final ResourceManager resourceManager;

    private final List<Selection<?>> results;

    public Result(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.results = new LinkedList<>();
    }

    public void addResultField(Expression<?> resultField) {
        results.add(resultField);
    }

    public List<Selection<?>> getResults() {
        return results;
    }

    public void finalizeResult() {
        this.resourceManager.getCriteriaQuery().multiselect(results);
    }

}
