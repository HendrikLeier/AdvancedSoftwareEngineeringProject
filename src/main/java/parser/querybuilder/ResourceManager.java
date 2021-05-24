package parser.querybuilder;

import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.generated.ParseException;
import persisted.Event;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;

public class ResourceManager {

    public ResourceManager(Root<Event> eventRoot, CriteriaQuery<Tuple> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        this.root = eventRoot;
        this.criteriaBuilder = criteriaBuilder;
        this.criteriaQuery = criteriaQuery;
    }

    private final Root<Event> root;
    private final CriteriaQuery<Tuple> criteriaQuery;
    private final CriteriaBuilder criteriaBuilder;
    private final Logger logger = LoggerFactory.getLogger(ResourceManager.class);


    /**
     * As it is impossible to determine which field is going to be queried it is impossible to determine the
     * type of it at compile time. This results in the use of a wildcard. Currently there is no other way in java as typechecking
     * at runtime is a concept unknown to java.
     * */
    public Expression<?> getReferencedField(String fieldName) throws FieldException {
        SimpleField simpleField = FieldHelper.getSimpleField(fieldName);
        if(simpleField == null)
            throw new FieldException("Could not find the fieldname");
        SingularAttribute<Event, ?> untypedAttribute = simpleField.getReferencedField();
        return root.get(untypedAttribute);
    }

    /**
     * @param fieldName the name of the field you wish to obtain a Expression-reference on
     * @param targetType The type you need this field as
     * @param <T> The type
     * @return An Expression with the desired type that references the db field
     * @throws FieldException If the desired field doesn't exist or it's type doesn't match the required one
     */
    public <T> Expression<T> getReferencedFieldAsType(String fieldName, Class<T> targetType) throws FieldException {
        SimpleField simpleField = FieldHelper.getSimpleField(fieldName);
        if(simpleField == null)
            throw new FieldException("Could not find the fieldname");
        SingularAttribute<Event, ?> untypedAttribute = simpleField.getReferencedField();
        if(untypedAttribute != null && isFieldOfType(untypedAttribute, targetType)) {
            // This should be safe as we checked type compatability before... Check this with the reference documentation tho
            return root.get(untypedAttribute).as(targetType);
        } else throw new FieldException("I was unable to obtain a reference to the database object '"+fieldName+"'. Most likely something internal broke...");
    }

    public <T> boolean isFieldOfType(String fieldName, Class<T> type) {
        logger.warn("Trying to determine type of "+fieldName);
        SimpleField simpleField = FieldHelper.getSimpleField(fieldName);
        if(simpleField == null)
            return false;
        SingularAttribute<Event, ?> untypedAttribute = simpleField.getReferencedField();
        if(untypedAttribute != null) {
            return isFieldOfType(untypedAttribute, type);
        }
        else return false;
    }


    public <T> boolean isFieldOfType(SingularAttribute<Event,?> field, Class<T> type) {
        if(field == null)
            return false;
        logger.warn("Check is "+field.getJavaType().getTypeName() + " >= "+type.getTypeName());
        boolean result = type.isAssignableFrom(field.getJavaType());
        logger.warn("Check resulted in "+result);
        return result;
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
