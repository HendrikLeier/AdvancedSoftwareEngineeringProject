package parser.querybuilder;

import javax.persistence.criteria.Expression;
import java.time.LocalDateTime;

public class ResultField {

    private final ResourceManager resourceManager;

    private final static Class[] aggregateNumericGroup = new Class[]{Number.class, LocalDateTime.class};

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
                if (resourceManager.isExpressionOfType(field, aggregateNumericGroup)) {
                    // 100% type safe as mentioned before.
                    return (Expression<Number>) resourceManager.getCriteriaBuilder().sum((Expression<? extends Number>) field);
                } else throw new FieldException("Wrong Expression type for sum aggregate");
            case "avg":
                if (resourceManager.isExpressionOfType(field, aggregateNumericGroup)) {
                    // 100% type safe as mentioned before. Just needed some trick to convince java that this can be compiled...
                    return (Expression<Number>) ((Object) resourceManager.getCriteriaBuilder().avg((Expression<? extends Number>) field));
                } else throw new FieldException("Wrong Expression type for avg aggregate");
            case "max":
                if (resourceManager.isExpressionOfType(field, aggregateNumericGroup)) {
                    // 100% type safe as mentioned before.
                    return (Expression<Number>) resourceManager.getCriteriaBuilder().max((Expression<? extends Number>) field);
                } else throw new FieldException("Wrong Expression type for max aggregate");
            case "min":
                if (resourceManager.isExpressionOfType(field, aggregateNumericGroup)) {
                    // 100% type safe as mentioned before.
                    return (Expression<Number>) resourceManager.getCriteriaBuilder().min((Expression<? extends Number>) field);
                } else throw new FieldException("Wrong Expression type for min aggregate");
            case "count":
                return resourceManager.getCriteriaBuilder().count(field).as(Number.class);
            default:
                throw new FieldException("aggregate Function in impossible state! Check parser");
        }
    }

}
