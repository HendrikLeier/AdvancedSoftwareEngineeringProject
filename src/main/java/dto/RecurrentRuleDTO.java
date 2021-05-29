package dto;

import persisted.RecurrentRuleReferencePointType;
import persisted.RecurrentRuleType;

public class RecurrentRuleDTO {

    private RecurrentRuleType type;

    private RecurrentRuleReferencePointType referencePointType;

    private String interval;

    public RecurrentRuleType getType() {
        return type;
    }

    public void setType(RecurrentRuleType type) {
        this.type = type;
    }

    public RecurrentRuleReferencePointType getReferencePointType() {
        return referencePointType;
    }

    public void setReferencePointType(RecurrentRuleReferencePointType referencePointType) {
        this.referencePointType = referencePointType;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
