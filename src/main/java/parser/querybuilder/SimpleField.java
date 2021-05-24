package parser.querybuilder;

import persisted.Event;
import persisted.Event_;

import javax.persistence.metamodel.SingularAttribute;


/** Fields that are actually part of the Events Entity */
public enum SimpleField {
    amount(Event_.amount), datetime(Event_.localDateTime), name(Event_.name), type(Event_.type), testBool(Event_.someTestField), actor(Event_.actor);

    private final SingularAttribute<Event, ?> singularAttribute;

    SimpleField(SingularAttribute<Event, ?> attribute) {
        this.singularAttribute = attribute;
    }
    /* Ok so java doesn't like generics in enum's apparently... Well then I guess you'll have to imagine them */
    public SingularAttribute<Event, ?> getReferencedField() {
        /* Sadly there is no other way with this pattern of mine */
        return singularAttribute;
    }
}
