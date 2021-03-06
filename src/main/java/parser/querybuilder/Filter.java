package parser.querybuilder;

import org.hibernate.Session;
import org.hibernate.cfg.NotYetImplementedException;
import parser.helper.Expr;
import persisted.Event;


import javax.persistence.criteria.*;
import java.util.*;

public class Filter {

    private CriteriaBuilder criteriaBuilder;

    private CriteriaQuery<Event> criteriaQuery;

    private Root<Event> root;

    private HashMap<ForeignField, Join> joinList;

    public Filter(CriteriaBuilder cb) {
        this.criteriaBuilder = cb;
        this.criteriaQuery = criteriaBuilder.createQuery(Event.class);
        this.root = this.criteriaQuery.from(Event.class);
    }

    public CriteriaQuery<Event> getCriteriaQuery() {
        return criteriaQuery;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public Root<Event> getRoot() {
        return root;
    }

    public HashMap<ForeignField, Join> getJoinList() {
        return joinList;
    }

    /* finalizer */

    public void finalizeQuery(Predicate predicate) {
        criteriaQuery.select(root).where(predicate);
    }

    /* Logic */

    public Predicate handleOr(Expression<Boolean> a, Expression<Boolean> b) {
        return criteriaBuilder.or(a, b);
    }

    public Predicate handleAnd(Expression<Boolean> a, Expression<Boolean> b) {
        return criteriaBuilder.and(a, b);
    }

    /* Binary Operators */

    public Predicate handleEqualObj(String fieldName, Object value) throws FieldException {
        return criteriaBuilder.equal(getReferencedField(fieldName), value);
    }

    public <T> Expression<T> getReferencedField(String fieldName) throws FieldException {
        SimpleField simpleField = FieldHelper.getSimpleField(fieldName);
        if (simpleField != null) {
            return root.get(simpleField.getReferencedField());
        }else {
            ForeignField foreignField = FieldHelper.getForeignField(fieldName);
            if (foreignField != null) {
                throw new NotYetImplementedException("Foreign Fields are not yet implemented!");
            }
        }

        throw new FieldException("Field "+fieldName+" not found!");
    }

    public Predicate handlePredicateList(List<Predicate> predicateList, LogicOperator op) {
        Predicate currPredicate = predicateList.get(0);
        for (int i = 1; i < predicateList.size(); i++) {
            switch (op) {
                case And:
                    currPredicate = criteriaBuilder.and(predicateList.get(i), currPredicate);
                    break;
                case Or:
                    currPredicate = criteriaBuilder.or(predicateList.get(i), currPredicate);
                    break;
            }
        }

        return currPredicate;
    }

}
