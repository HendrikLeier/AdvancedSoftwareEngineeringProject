package parser.querybuilder;


import parser.generated.ParseException;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class FilterSelector extends LogicSelector{
    public FilterSelector(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    public void finalizeSelector() {
        if(this.filterPredicate != null) {
            this.resourceManager.getCriteriaQuery().where(this.filterPredicate);
        }
    }


    @Override
    public <X> Expression<X> getReferencedFieldOfType(String fieldName, Class<X> type) throws FieldException {
        return this.resourceManager.getReferencedFieldAsType(fieldName, type);
    }

    @Override
    public <Y> Expression<Number> getAggregateOf(String aggregateName, Expression<Y> field) throws ParseException
    {
        throw new ParseException("Aggregate not allowed here!");
    }

    public void setFilterPredicate(Predicate predicate) {
        this.setSelectorPredicate(predicate);
    }

}
