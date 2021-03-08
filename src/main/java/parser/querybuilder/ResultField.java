package parser.querybuilder;

import javax.persistence.criteria.Expression;

public class ResultField {

    private final ResourceManager resourceManager;

    public ResultField(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public <X> Expression<? extends X> handleResultField(String fieldName, String aggregateName) throws FieldException {
        Expression<?> field = resourceManager.getReferencedField(fieldName);
        if(aggregateName != null) {
            if (!aggregateName.equals("count")) {

                Expression<? extends Number> number_field = (Expression<? extends Number>) field;

                switch (aggregateName) {
                    case "sum":
                        return (Expression<? extends X>) resourceManager.getCriteriaBuilder().sum(number_field);
                    case "avg":
                        return (Expression<? extends X>) resourceManager.getCriteriaBuilder().avg(number_field);
                    case "max":
                        return (Expression<? extends X>) resourceManager.getCriteriaBuilder().max(number_field);
                    case "min":
                        return (Expression<? extends X>) resourceManager.getCriteriaBuilder().min(number_field);
                    default:
                        throw new FieldException("Aggregate Handler in unreachable state, check parser!");
                }
            } else {
                return (Expression<? extends X>) resourceManager.getCriteriaBuilder().count(field);
            }
        } else return (Expression<? extends X>) field;
    }

}
