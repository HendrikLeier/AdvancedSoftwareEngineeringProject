package parser.querybuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class WithClauseSelector extends LogicSelector {


    public WithClauseSelector(ResourceManager resourceManager) {
        super(resourceManager);

    }

    @Override
    public void finalizeSelector() {
        if (this.filterPredicate != null) {
            this.resourceManager.getCriteriaQuery().having(this.filterPredicate);
        }
    }

    @Override
    public <X> Expression<X> getReferencedFieldOfType(String fieldName, Class<X> type) throws FieldException {
        return this.resourceManager.getReferencedFieldAsType(fieldName, type);
    }

    @Override
    public <Y> Expression<Number> getAggregateOf(String aggregateName, Expression<Y> field) throws FieldException {
        switch (aggregateName) {
            case "sum":
                return resourceManager.getCriteriaBuilder().sum(field.as(Number.class));
            case "avg":
                return resourceManager.getCriteriaBuilder().avg(field.as(Number.class)).as(Number.class);
            case "max":
                return resourceManager.getCriteriaBuilder().max(field.as(Number.class));
            case "min":
                return resourceManager.getCriteriaBuilder().min(field.as(Number.class));
            case "count":
                return resourceManager.getCriteriaBuilder().count(field).as(Number.class);
            default:
                throw new FieldException("Aggreagte function in impossible state! Check parser!");
        }
    }

    public void setWithPredicate(Predicate predicate) {
        this.setSelectorPredicate(predicate);
    }

}
