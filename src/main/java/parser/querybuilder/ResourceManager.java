package parser.querybuilder;

import org.hibernate.cfg.NotYetImplementedException;
import persisted.Event;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;

public class ResourceManager {

    public ResourceManager(Root<Event> eventRoot, CriteriaQuery<Tuple> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        this.root = eventRoot;
        this.criteriaBuilder = criteriaBuilder;
        this.criteriaQuery = criteriaQuery;
    }

    private final Root<Event> root;
    private final CriteriaQuery<Tuple> criteriaQuery;
    private final CriteriaBuilder criteriaBuilder;

    public <T> Expression<? extends T> getReferencedField(String fieldName) throws FieldException {
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

    public Root<Event> getRoot() {
        return root;
    }

    public CriteriaQuery<Tuple> getCriteriaQuery() {
        return criteriaQuery;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public void finalizeQuery(Predicate predicate) {
        criteriaQuery.where(predicate);
    }
}
