package parser.querybuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class WithClauseSelector extends LogicSelector {

    private final ResultField resultField;

    public WithClauseSelector(ResourceManager resourceManager, ResultField resultField) {
        super(resourceManager);
        this.resultField = resultField;
    }

    @Override
    public void finalizeSelector() {
        if (this.filterPredicate != null) {
            this.resourceManager.getCriteriaQuery().having(this.filterPredicate);
        }
    }

    @Override
    public <X> Expression<? extends X> getReferencedField(String fieldName, String aggregateName) throws FieldException {
        return resultField.handleResultField(fieldName, aggregateName);
    }

    public void setWithPredicate(Predicate predicate) {
        this.setSelectorPredicate(predicate);
    }

}
