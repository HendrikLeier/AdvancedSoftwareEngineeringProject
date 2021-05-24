package parser.querybuilder;

import javax.persistence.criteria.Expression;

public class ResultField {

    private final ResourceManager resourceManager;

    public ResultField(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public Expression<?> handleResultField(String fieldName, String aggregateName) throws FieldException {
        Expression<?> field = resourceManager.getReferencedField(fieldName);
        if (aggregateName != null) {
            return aggregate(aggregateName, field);
        }
        return field;
    }

    private <X> Expression<Number> aggregate(String aggregateName, Expression<X> field) throws FieldException {
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
                return resourceManager.getCriteriaBuilder().count(field.as(Number.class)).as(Number.class);
            default:
                throw new FieldException("aggregate Function in impossible state! Check parser");
        }
    }

}
