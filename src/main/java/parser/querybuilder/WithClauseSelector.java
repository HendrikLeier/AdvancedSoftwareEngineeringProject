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
                return resourceManager.getCriteriaBuilder().sum((Expression<Number>) field);
            case "avg":
                return (Expression<Number>) (Object) resourceManager.getCriteriaBuilder().avg((Expression<Number>) field);
            case "max":
                return resourceManager.getCriteriaBuilder().max((Expression<Number>) field);
            case "min":
                return resourceManager.getCriteriaBuilder().min((Expression<Number>) field);
            case "count":
                return (Expression<Number>) (Object) resourceManager.getCriteriaBuilder().count(field);
            default:
                throw new FieldException("Aggreagte function in impossible state! Check parser!");
        }
    }

    public void setWithPredicate(Predicate predicate) {
        this.setSelectorPredicate(predicate);
    }

}
