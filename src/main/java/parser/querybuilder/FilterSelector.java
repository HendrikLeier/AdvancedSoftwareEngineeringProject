package parser.querybuilder;


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
    public <X> Expression<? extends X> getReferencedField(String fieldName, String aggregateName) throws FieldException {
        if (aggregateName != null) {
            throw new FieldException("No aggregates in selection!");
        }
        return this.resourceManager.getReferencedField(fieldName);
    }

    public void setFilterPredicate(Predicate predicate) {
        this.setSelectorPredicate(predicate);
    }

}
