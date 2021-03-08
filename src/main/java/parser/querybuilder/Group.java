package parser.querybuilder;

import javax.persistence.criteria.Expression;
import java.util.*;

public class Group {

    private final ResourceManager resourceManager;

    private final List<Expression<?>> fields;

    private final WithClauseSelector withClauseSelector;

    public WithClauseSelector getWithClauseSelector() {
        return withClauseSelector;
    }

    public Group(ResourceManager resourceManager, ResultField resultField) {
        this.resourceManager = resourceManager;
        this.fields = new LinkedList<>();
        this.withClauseSelector = new WithClauseSelector(resourceManager, resultField);
    }

    public void addField(String fieldName) throws FieldException {
        Expression<?> field = resourceManager.getReferencedField(fieldName);
        fields.add(field);
    }

    public void finalizeGroup() {
        resourceManager.getCriteriaQuery().groupBy(fields);
        this.withClauseSelector.finalizeSelector();
    }

}
