package parser.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persisted.*;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.time.Duration;
import java.util.*;

/**
 * Takes care of acquiring references to the corresponding database resources
 * This includes initial resolving of paths (joins, selects...) as well as caching them for performance reasons
 */
public class ResourceManager {

    public ResourceManager(Root<Event> eventRoot, CriteriaQuery<Tuple> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        this.root = eventRoot;
        this.criteriaBuilder = criteriaBuilder;
        this.criteriaQuery = criteriaQuery;
        /*
         * The Maps used for caching (tree-maps should be most efficient for the amounts of data)
         */
        this.sourceCache = new TreeMap<>();
        this.fieldCache = new TreeMap<>();
    }

    private final Root<Event> root;
    private final CriteriaQuery<Tuple> criteriaQuery;
    private final CriteriaBuilder criteriaBuilder;
    private final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    public Map<FetchReceipt, FetchResolveResult> getSourceCache() {
        return sourceCache;
    }

    /* Cannot be final as it ist changed by a test */
    private Map<FetchReceipt, FetchResolveResult> sourceCache;
    private final Map<FetchReceipt, Expression<?>> fieldCache;

    public void setSourceCache(Map<FetchReceipt, FetchResolveResult> sourceCache) {
        this.sourceCache = sourceCache;
    }

    /**
     * Type mappings for referenced classes
     * --> Target classes for some types
     */
    private static final HashMap<Class, Class> classClassHashMap = new HashMap<>()
            {{
                put(persisted.RecurrentEvent.class, UUID.class);
                put(EventType.class, String.class);
                put(Actor.class, UUID.class);
                put(RecurrentRuleType.class, String.class);
                put(RecurrentRuleReferencePointType.class, String.class);
                put(Duration.class, Long.class);
            }};

    /**
     * @param field The class to be checked
     * @return Either the target class or the input class
     */
    private Class<?> replaceClass(Class<?> field) {
        return classClassHashMap.getOrDefault(field, field);
    }

    /**
     * A method that resolves fields that require joins to accessed
     * As a general rule only left joins are used, as inner joins would discard all tuples that don't match
     * and right joins as well as outer joins make no sense at all
     * @param fetchReceipt The FetchReceipt that describes the path to the attribute
     * @return The FetchResolveResult
     */
    private FetchResolveResult resolveReceipt(FetchReceipt fetchReceipt) {
        if(!sourceCache.containsKey(fetchReceipt)) {
            Join<?, ?> currentFetchResult = root.join((SingularAttribute) fetchReceipt.getFetchList().get(0), JoinType.LEFT);
            // Type cannot be determined at compile time...
            for (int i = 1; i < fetchReceipt.getFetchList().size() - 1; i++) {
                // No type determination at compile time possible.
                currentFetchResult = currentFetchResult.join((SingularAttribute) fetchReceipt.getFetchList().get(i), JoinType.LEFT);
            }
            SingularAttribute<?, ?> untypedAttribute = fetchReceipt.getFetchList().get(fetchReceipt.getFetchList().size() - 1);

            FetchResolveResult fetchResolveResult = new FetchResolveResult(currentFetchResult, untypedAttribute);
            sourceCache.put(fetchReceipt, fetchResolveResult);

            return fetchResolveResult;
        } else return sourceCache.get(fetchReceipt);
    }

    /**
     * As it is impossible to determine which field is going to be queried it is impossible to determine the
     * type of it at compile time. This results in the use of a wildcard. Currently there is no other way in java as typechecking
     * at runtime is a concept unknown to java.
     * */
    public Expression<?> getReferencedField(String fieldName) throws FieldException {
        DataModelField dataModelField = FieldHelper.getDataModelField(fieldName);
        if(dataModelField == null)
            throw new FieldException("Could not find the fieldname");
        SingularAttribute<?, ?> untypedAttribute = dataModelField.getReferencedField();

        Join<?, ?> currentFetchResult = null;

        if (dataModelField.requiresFetching()) {
            FetchResolveResult fetchResolveResult = resolveReceipt(dataModelField.getFetchReceipt());
            currentFetchResult = fetchResolveResult.resultBundle;
            untypedAttribute = fetchResolveResult.specificAttribute;
        }
        Class<?> targetClass = replaceClass(untypedAttribute.getJavaType());

        Expression<?> result = getField(dataModelField, untypedAttribute, currentFetchResult);

        if (targetClass == untypedAttribute.getJavaType()) {
            return result;
        }else {
            return result.as(targetClass);
        }

    }

    /**
     * This class is there cause I didn't want to write this twice...
     * @param dataModelField The DataModelField object for the desired field
     * @param untypedAttribute The Singular attribute referencing the database object
     * @param currentFetchResult The current join result (either cached or calculated)
     * @return The Expression
     */
    private Expression<?> getField(DataModelField dataModelField, SingularAttribute<?,?> untypedAttribute, Join<?,?> currentFetchResult) {
        Expression<?> result;
        if(!fieldCache.containsKey(dataModelField.getFetchReceipt())) {
            if (!dataModelField.requiresFetching()) {
                result = root.get((SingularAttribute<? super Event, ? extends Object>) untypedAttribute);
            } else {
                result = currentFetchResult.get((SingularAttribute) untypedAttribute);
            }
            /* Cache for query optimization */
            fieldCache.put(dataModelField.getFetchReceipt(), result);
        } else {
            result = fieldCache.get(dataModelField.getFetchReceipt());
        }

        return result;
    }

    /**
     * A class which is used to store fetch results
     */
    public static class FetchResolveResult {
        /**
         * A variable used to store the Join-object which contains the attribute
         */
        private final Join<?,?> resultBundle;
        /**
         * The SingulareAttribute that is part of the Join-object and contains the field reference
         */
        private final SingularAttribute<?, ?> specificAttribute;

        public FetchResolveResult(Join<?, ?> resultBundle, SingularAttribute<?, ?> specificAttribute) {
            this.resultBundle = resultBundle;
            this.specificAttribute = specificAttribute;
        }

        public Join<?, ?> getResultBundle() {
            return resultBundle;
        }

        public SingularAttribute<?, ?> getSpecificAttribute() {
            return specificAttribute;
        }
    }

    /**
     * @param fieldName the name of the field you wish to obtain a Expression-reference on
     * @param targetType The type you need this field as
     * @param <T> The type
     * @return An Expression with the desired type that references the db field
     * @throws FieldException If the desired field doesn't exist or it's type doesn't match the required one
     */
    public <T> Expression<T> getReferencedFieldAsType(String fieldName, Class<T> targetType) throws FieldException {
        DataModelField dataModelField = FieldHelper.getDataModelField(fieldName);
        if(dataModelField == null)
            throw new FieldException("Could not find the fieldname "+fieldName);
        SingularAttribute<?, ?> untypedAttribute = dataModelField.getReferencedField();
        // Recursively fetch if required
        Join<?, ?> currentFetchResult = null;

        if (dataModelField.requiresFetching()) {
            FetchResolveResult fetchResolveResult = resolveReceipt(dataModelField.getFetchReceipt());
            currentFetchResult = fetchResolveResult.resultBundle;
            untypedAttribute = fetchResolveResult.specificAttribute;
        }

        if(untypedAttribute != null && isFieldOfType(untypedAttribute, targetType)) {

            Expression<?> result = getField(dataModelField, untypedAttribute, currentFetchResult);
            // This is 100% typesafe as the "isFieldOfType" method validated it. The java compiler is just too fucking dumb to realize it!
            if (!isFieldCastingRequired(untypedAttribute, targetType)) {
                return (Expression<T>) result;
            } else {
                return result.as(targetType);
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
        logger.debug("Trying to determine type of "+fieldName);
        DataModelField dataModelField = FieldHelper.getDataModelField(fieldName);
        if(dataModelField == null)
            return false;
        SingularAttribute<?, ?> untypedAttribute = dataModelField.getReferencedField();
        if (dataModelField.requiresFetching()) {
            FetchResolveResult fetchResolveResult = resolveReceipt(dataModelField.getFetchReceipt());
            untypedAttribute = fetchResolveResult.specificAttribute;
        }

        if(untypedAttribute != null) {
            return isFieldOfType(untypedAttribute, type);
        }
        else return false;
    }

    /**
     * Method that determines if casting is necessary before returning
     * @param field The field you wanna check
     * @param type The type you wanna check for
     * @return True if casting is necessary, False if not
     */
    public <T> boolean isFieldCastingRequired(SingularAttribute<?,?> field, Class<T> type) {
        if(field == null)
            return false;
        boolean castingAvoidable = type.isAssignableFrom(field.getJavaType());
        if (!castingAvoidable) {
            logger.debug("Casting required for "+field.getJavaType().getTypeName());
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
    public <T> boolean isFieldOfType(SingularAttribute<?,?> field, Class<T> type) {
        if(field == null)
            return false;
        logger.debug("Check is "+field.getJavaType().getTypeName() + " >= "+type.getTypeName());
        boolean result = type.isAssignableFrom(replaceClass(field.getJavaType()));
        logger.debug("Check resulted in "+result);
        return result;
    }

    /**
     * Checks if the type of the Expression is a 'descendant' of a type
     * @param field The field you wanna check
     * @param type The type you wanna check for
     * @return True if the type is related, False if not
     */
    public <T> boolean isExpressionOfType(Expression<?> field, Class<T> type) {
        if(field == null)
            return false;
        return type.isAssignableFrom(field.getJavaType());
    }

    /**
     * Same method as above just with list of types
     * @param field The field you wanna check
     * @param classes The types you wanna check for
     * @return True if one of the types is related, False if not
     */
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
}
