package parser.querybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persisted.*;

import javax.persistence.metamodel.SingularAttribute;


/** Fields that are actually part of the Events Entity */
public enum DataModelField {
    amount(Event_.amount),
    datetime(Event_.localDateTime),
    id(Event_.eventId),
    name(Event_.name),
    type(Event_.type),
    testBool(Event_.someTestField),
    actorId(Event_.actor, new FetchReceipt(Actor_.actorId)),
    actorName(Event_.actor, new FetchReceipt(Actor_.name)),
    actorDescription(Event_.actor, new FetchReceipt(Actor_.description)),
    recurrentEventId(Event_.recurrentParent, new FetchReceipt(RecurrentEvent_.recurrentEventId)),
    recurrentEventName(Event_.recurrentParent, new FetchReceipt(RecurrentEvent_.name)),
    recurrentRuleType(Event_.recurrentParent, new FetchReceipt(RecurrentEvent_.rule, RecurrentRule_.type)),
    recurrentRuleId(Event_.recurrentParent, new FetchReceipt(RecurrentEvent_.rule, RecurrentRule_.ruleUUID)),
    recurrentRuleReferencePoint(Event_.recurrentParent, new FetchReceipt(RecurrentEvent_.rule, RecurrentRule_.referencePointType)),
    recurrentRuleInterval(Event_.recurrentParent, new FetchReceipt(RecurrentEvent_.rule, RecurrentRule_.interval));

    private final SingularAttribute<Event, ?> singularAttribute;

    private FetchReceipt fetchReceipt;

    DataModelField(SingularAttribute<Event, ?> attribute) {
        this.singularAttribute = attribute;
        this.fetchReceipt = new FetchReceipt();
        this.fetchReceipt.emplaceFront(attribute);
    }

    DataModelField(SingularAttribute<Event, ?> attribute, FetchReceipt fetchReceipt) {
        Logger logger = LoggerFactory.getLogger(DataModelField.class);
        this.singularAttribute = attribute;
        this.fetchReceipt = fetchReceipt;
        // To make the Receipt complete
        this.fetchReceipt.emplaceFront(attribute);
        logger.info("Object initialized");
    }

    /* Ok so java doesn't like generics in enum's apparently... Well then I guess you'll have to imagine them */
    public SingularAttribute<Event, ?> getReferencedField() {
        /* Sadly there is no other way with this pattern of mine */
        return singularAttribute;
    }

    public boolean requiresFetching() {
        return this.fetchReceipt.getFetchList().size() > 1;
    }

    public FetchReceipt getFetchReceipt() {
        return fetchReceipt;
    }
}
