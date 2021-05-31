package parser.querybuilder;

import parser.generated.ParseException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.util.*;

public class Group {

    private final ResourceManager resourceManager;

    private final List<Expression<?>> groups;

    private final LogicSelector withClauseSelector;

    public LogicSelector getLogicSelector() {
        return withClauseSelector;
    }

    public Group(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.groups = new LinkedList<>();
        this.withClauseSelector = new WithClauseSelector(resourceManager);
    }

    public Group(ResourceManager resourceManager, LogicSelector withClauseSelector) {
        this.resourceManager = resourceManager;
        this.groups = new LinkedList<>();
        this.withClauseSelector = withClauseSelector;
    }

    public void addField(String fieldName) throws FieldException {
        Expression<?> field = resourceManager.getReferencedField(fieldName);
        groups.add(field);
    }

    public Expression<Integer> timeBin(String fieldName, String binType) throws ParseException {
        Expression<?> field = resourceManager.getReferencedField(fieldName);

        if(groups.contains(field)) {
            throw new ParseException("If the field is part of a bin it cannot be part of a group at the same time!");
        }
        // TODO: check if this can be done more elegant
        CriteriaBuilder cb = resourceManager.getCriteriaBuilder();
        Expression<Integer> timeBin = null;
        if(binType.equals("year")) {
            timeBin = cb.function("year", Integer.class, field);
        } else if(binType.equals("month")) {
            timeBin = cb.sum(
                    cb.prod(cb.function("year", Integer.class, field), 12)
                    ,cb.prod(cb.function("month", Integer.class, field), 1));

        } else if(binType.equals("day")) {
            timeBin = cb.sum(
                    cb.prod(cb.function("year", Integer.class, field), 12 * 31)
                    , cb.sum(cb.prod(cb.function("month", Integer.class, field), 31)
                            , cb.function("day", Integer.class, field)));
        } else if(binType.equals("hour")) {
            timeBin = cb.sum(
                    cb.prod(cb.function("year", Integer.class, field), 12 * 31 * 24)
                    , cb.sum(cb.prod(cb.function("month", Integer.class, field), 31 * 24)
                            , cb.sum(cb.prod(cb.function("day", Integer.class, field), 24),
                            cb.function("hour", Integer.class, field))));
        } else {
            throw new ParseException("unknown bin type specified");
        }
        groups.add(timeBin);

        return timeBin;
    }

    public void finalizeGroup() {
        resourceManager.getCriteriaQuery().groupBy(groups);
        this.withClauseSelector.finalizeSelector();
    }

}
