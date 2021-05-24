package parser.querybuilder;

import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.generated.ParseException;
import persisted.Actor;
import persisted.Event;
import persisted.EventType;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.HashMap;
import java.util.UUID;

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
     * Type mappings for referenced classes
     *
     * TODO: ad other direction to hash map
     * */
    private final HashMap<Class, Class> classClassHashMap = new HashMap<>()
            {{
                put(persisted.RecurrentEvent.class, UUID.class);
                put(EventType.class, String.class);
                put(Actor.class, UUID.class);
            }};

    private Class<?> replaceClass(Class<?> field) {
        return classClassHashMap.getOrDefault(field, field);
    }

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
            // This is 100% typesafe as the "isFieldOfType" method validated it. The java compiler is just too fucking dumb to realize it!

            if(!isFieldCastingRequired(untypedAttribute, targetType)) {
                return (Expression<T>) root.get(untypedAttribute);
            }else {
                return root.get(untypedAttribute).as(targetType);
            }
        } else throw new FieldException("I was unable to obtain a reference to the database object '"+fieldName+"'. Most likely something internal broke...");
    }

    /**
     * Determines whether or not the field provided by
     * @param fieldName the field in question (as a string)
     * is assignable to to
     * @param type the class which it is checked for
     * @return true if so false if not
     * */
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

    public <T> boolean isFieldCastingRequired(SingularAttribute<Event,?> field, Class<T> type) {
        if(field == null)
            return false;
        boolean castingAvoidable = type.isAssignableFrom(field.getJavaType());
        if (!castingAvoidable) {
            logger.warn("Casting required for "+field.getJavaType().getTypeName());
        }
        return !castingAvoidable;
    }

    /**
     * Determines whether or not the field provided by
     * @param field The field in question (as a SingularAttribute)
     * is assignable to to
     * @param type The type to check for
     * @return True if so else False
     *
     * ###### Watch out with primitives! ######
     * For the database there is no difference in say double and java.lang.Double...
     * For the typechecker however there is. In the CQL parser which requires classes which can be assigned to
     * java.lang.Number for a field of type double this function would yield false while a field of java.lang.Double
     * would yield true. If you want to use primitives the easiest way is to convert them to the wrapper classes.
     */
    public <T> boolean isFieldOfType(SingularAttribute<Event,?> field, Class<T> type) {
        if(field == null)
            return false;
        logger.warn("Check is "+field.getJavaType().getTypeName() + " >= "+type.getTypeName());
        boolean result = type.isAssignableFrom(replaceClass(field.getJavaType()));
        logger.warn("Check resulted in "+result);
        return result;
    }

    public <T> boolean isExpressionOfType(Expression<?> field, Class<T> type) {
        if(field == null)
            return false;
        return type.isAssignableFrom(field.getJavaType());
    }

    public boolean isExpressionOfType(Expression<?> field, Class[] classes) {
        for (Class c : classes) {
            if(isExpressionOfType(field, c)) {
                return true;
            }
        }
        return false;
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
