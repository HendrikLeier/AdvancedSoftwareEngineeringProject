package parser.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.generated.ParseException;


import javax.persistence.criteria.*;
import java.util.*;

public abstract class LogicSelector {

    private static final Logger logger = LoggerFactory.getLogger(LogicSelector.class);

    protected final ResourceManager resourceManager;

    public LogicSelector(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.arithmeticSelector = new ArithmeticSelector(this);
    }

    protected Predicate filterPredicate;

    protected ArithmeticSelector arithmeticSelector;

    public void setSelectorPredicate(Predicate filterPredicate) {
        this.filterPredicate = filterPredicate;
    }

    public abstract void finalizeSelector();

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ArithmeticSelector getArithmeticSelector() {
        return arithmeticSelector;
    }

    /* Binary Operators */

    public Predicate handleEqualObj(Expression v1, Expression v2) throws FieldException {
        return resourceManager.getCriteriaBuilder().equal(v1, v2);
    }

    public Predicate handleGreaterObj(Expression v1, Expression v2, boolean equal) throws FieldException {
            try {
                if (!equal)
                    return resourceManager.getCriteriaBuilder().greaterThan(v1, v2);
                else
                    return resourceManager.getCriteriaBuilder().greaterThanOrEqualTo(v1, v2);
            } catch (NumberFormatException e) {
                throw new FieldException("Wrong value format of value "+v2+" for field "+v1);
            }
    }

    public <Y extends Comparable<? super Y>> Predicate handleSmallerObj(Expression v1, Expression v2, boolean equal) throws FieldException {

        try {
            if(!equal)
                return resourceManager.getCriteriaBuilder().lessThan(v1, v2);
            else
                return resourceManager.getCriteriaBuilder().lessThanOrEqualTo(v1, v2);
        } catch (NumberFormatException e) {
            throw new FieldException("Wrong value format of value "+v1+ " with type "+ v1.getClass().getTypeName() +" for field "+v2 + " with type " + v2.getJavaType().getTypeName());
        }
    }

    public Predicate handleLike(Expression v1, Expression v2) throws FieldException {
        return resourceManager.getCriteriaBuilder().like(v1, v2);
    }

    public <Y extends Comparable<? super Y>> Predicate handleBetween(Expression v1, Expression v2, Expression v3) throws FieldException {
        return resourceManager.getCriteriaBuilder().between(v1, v2, v3);
    }

    public Predicate handleStartswith(Expression v1, Expression v2) throws FieldException {
        return handleLike(v1, this.getResourceManager().getCriteriaBuilder().concat(v2, "%"));
    }

    public Predicate handleEndswith(Expression v1, Expression v2) throws FieldException {
        return handleLike(v1, this.getResourceManager().getCriteriaBuilder().concat("%", v2));
    }

    public Predicate handleContains(Expression v1, Expression v2) throws FieldException {
        return handleLike(v1, this.getResourceManager().getCriteriaBuilder().concat("%",
                this.getResourceManager().getCriteriaBuilder().concat(v2, "%")
        ));
    }

    public Predicate handleBoolean(Expression<Boolean> bool) {
        return resourceManager.getCriteriaBuilder().isTrue(bool);
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


    public abstract <X> Expression<X> getReferencedFieldOfType(String fieldName, Class<X> type) throws FieldException;

    public abstract <Y> Expression<Number> getAggregateOf(String aggregateName, Expression<Y> field) throws FieldException, ParseException;
}
