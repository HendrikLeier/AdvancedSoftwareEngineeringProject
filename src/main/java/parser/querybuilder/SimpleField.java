package parser.querybuilder;

import persisted.Event;
import persisted.Event_;

import javax.persistence.metamodel.SingularAttribute;


/** Fields that are actually part of the Events Entity */
public enum SimpleField {
    amount(Event_.amount), datetime(Event_.localDateTime), name(Event_.name), type(Event_.type);

    private final SingularAttribute referencedField;

    SimpleField(SingularAttribute attribute) {
        this.referencedField = attribute;
    }
    /* Ok so java doesn't like generics in enum's apparently... Well then I guess you'll have to imagine them */
    public <T> SingularAttribute<Event, T> getReferencedField() {
        return referencedField;
    }
}
