package persisted;

import javax.persistence.*;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

// This is a ValueObject
@Entity
public class RecurrentRule {

    private RecurrentRule() {
        this.ruleUUID = UUID.randomUUID();
    }

    public RecurrentRule(Set<Duration> intervals) {
        this();
        this.type = RecurrentRuleType.interval;
        this.referencePointType = null;
        this.intervals = intervals;
    }

    public RecurrentRule(RecurrentRuleReferencePointType referencePointType, Set<Duration> offsets) {
        this();
        this.type = RecurrentRuleType.beginBased;
        this.referencePointType = referencePointType;
        this.intervals = offsets;
    }

    // Make this hash generated later
    @Id
    private final UUID ruleUUID;

    @Enumerated(EnumType.STRING)
    private RecurrentRuleType type;

    @Enumerated(EnumType.STRING)
    private RecurrentRuleReferencePointType referencePointType;

    @ElementCollection
    @CollectionTable(name="interval_sets", joinColumns=@JoinColumn(name="rule_id"))
    @Column(name="interval")
    private Set<Duration> intervals;

    public RecurrentRuleType getType() {
        return type;
    }

    public RecurrentRuleReferencePointType getReferencePointType() {
        return referencePointType;
    }

    public Set<Duration> getIntervals() {
        return intervals;
    }

    public UUID getRuleUUID() {
        return ruleUUID;
    }
}
