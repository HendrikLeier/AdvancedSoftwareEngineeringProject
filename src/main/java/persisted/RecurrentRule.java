package persisted;

import javax.persistence.*;
import java.time.Duration;
import java.util.UUID;

// This is a ValueObject --> Change this to not have an Identity
@Entity
public class RecurrentRule {

    private RecurrentRule() {
        this.ruleUUID = UUID.randomUUID();
    }

    public RecurrentRule(Duration interval) {
        this();
        this.type = RecurrentRuleType.interval;
        this.referencePointType = null;
        this.interval = interval;
    }

    public RecurrentRule(RecurrentRuleReferencePointType referencePointType, Duration offset) {
        this();
        this.type = RecurrentRuleType.beginBased;
        this.referencePointType = referencePointType;
        this.interval = offset;
    }

    // Make this hash generated later
    @Id
    private final UUID ruleUUID;

    @Enumerated(EnumType.STRING)
    private RecurrentRuleType type;

    @Enumerated(EnumType.STRING)
    private RecurrentRuleReferencePointType referencePointType;

    private Duration interval;

    public RecurrentRuleType getType() {
        return type;
    }

    public RecurrentRuleReferencePointType getReferencePointType() {
        return referencePointType;
    }

    public Duration getInterval() {
        return interval;
    }

    public UUID getRuleUUID() {
        return ruleUUID;
    }
}
