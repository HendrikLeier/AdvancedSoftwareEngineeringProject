package parser.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.criteria.*;
import java.util.*;

public abstract class LogicSelector {

    private static final Logger logger = LoggerFactory.getLogger(LogicSelector.class);

    protected final ResourceManager resourceManager;

    public LogicSelector(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    protected Predicate filterPredicate;

    public void setSelectorPredicate(Predicate filterPredicate) {
        this.filterPredicate = filterPredicate;
    }

    public abstract void finalizeSelector();

    /* Binary Operators */

    public Predicate handleEqualObj(String fieldName, String aggregateName, Object value) throws FieldException {
        return resourceManager.getCriteriaBuilder().equal(getReferencedField(fieldName, aggregateName), value);
    }

    public <Y extends Comparable<? super Y>> Predicate handleGreaterObj(String fieldName, String aggregateName, Object value, boolean equal) throws FieldException {
        Expression<? extends Y> field = getReferencedField(fieldName, aggregateName);

        if (value instanceof Comparable<?>) {
            Y exp_val = (Y) value;
            try {
                if (!equal)
                    return resourceManager.getCriteriaBuilder().greaterThan(field, exp_val);
                else
                    return resourceManager.getCriteriaBuilder().lessThanOrEqualTo(field, exp_val);
            } catch (NumberFormatException e) {
                throw new FieldException("Wrong value format of value "+value+" for field "+fieldName);
            }
        } else {
            throw new FieldException("Value not instanceof Comparable!");
        }
    }

    public <Y extends Comparable<? super Y>> Predicate handleSmallerObj(String fieldName, String aggregateName, Object value, boolean equal) throws FieldException {
        Expression<? extends Y> field = getReferencedField(fieldName, aggregateName);

        if (value instanceof Comparable<?>) {
            Y expectedValue = (Y) value;
            try {
                if(!equal)
                    return resourceManager.getCriteriaBuilder().lessThan(field, expectedValue);
                else
                    return resourceManager.getCriteriaBuilder().lessThanOrEqualTo(field, expectedValue);
            } catch (NumberFormatException e) {
                throw new FieldException("Wrong value format of value "+value+ " with type "+ value.getClass().getTypeName() +" for field "+fieldName + " with type " + field.getJavaType().getTypeName());
            }
        } else {
            throw new FieldException("Value not instanceof Comparable!");
        }
    }

    public Predicate handleLike(String fieldName, String aggregateName, Object value) throws FieldException {
        Expression field = getReferencedField(fieldName, aggregateName);
        if (value instanceof String && field.getJavaType() == String.class) {
            return resourceManager.getCriteriaBuilder().like(field, (String) value);
        }else {
            throw new FieldException("Value and Field must be of type string to be compared via 'like'");
        }
    }

    public <Y extends Comparable<? super Y>> Predicate handleBetween(String fieldName, String aggregateName, Object value1, Object value2) throws FieldException {
        Expression field =  getReferencedField(fieldName, aggregateName);

        if(value1 instanceof Comparable<?> && value2 instanceof Comparable<?>) {
            Y expectedValue1 = (Y) value1;
            Y expectedValue2 = (Y) value2;

            return resourceManager.getCriteriaBuilder().between(field, expectedValue1, expectedValue2);
        }else {
            throw new FieldException("Values have to be Comparables!");
        }
    }

    public Predicate handleStartswith(String fieldName, String aggregateName, Object value) throws FieldException {
        if(value instanceof String) {
            return handleLike(fieldName, aggregateName, value + "%");
        }else {
            throw new FieldException("'startswith' can only be used with Strings");
        }
    }

    public Predicate handleEndswith(String fieldName, String aggregateName, Object value) throws FieldException {
        if(value instanceof String) {
            return handleLike(fieldName, aggregateName, "%" + value);
        }else {
            throw new FieldException("'endswith' can only be used with Strings");
        }
    }

    public Predicate handleContains(String fieldName, String aggregateName, Object value) throws FieldException {
        if(value instanceof String) {
            return handleLike(fieldName, aggregateName, "%" + value + "%");
        }else {
            throw new FieldException("'endswith' can only be used with Strings");
        }
    }

    public Predicate handlePredicateList(List<Predicate> predicateList, LogicOperator op) {
        Predicate currPredicate = predicateList.get(0);
        for (int i = 1; i < predicateList.size(); i++) {
            switch (op) {
                case And:
                    currPredicate = resourceManager.getCriteriaBuilder().and(predicateList.get(i), currPredicate);
                    break;
                case Or:
                    currPredicate = resourceManager.getCriteriaBuilder().or(predicateList.get(i), currPredicate);
                    break;
            }
        }

        return currPredicate;
    }

    public abstract <X> Expression<? extends X> getReferencedField(String fieldName, String aggregateName) throws FieldException;

}
