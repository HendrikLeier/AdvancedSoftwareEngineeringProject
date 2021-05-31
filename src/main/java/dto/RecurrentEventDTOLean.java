package dto;

import persisted.EventType;

import java.util.UUID;

public class RecurrentEventDTOLean {

    private String startPoint;

    private String endPoint;

    private EventType type;

    private String name;

    private double amount;

    private UUID actorId;

    private RecurrentRuleDTO recurrentRuleDTO;

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public RecurrentRuleDTO getRecurrentRuleDTO() {
        return recurrentRuleDTO;
    }

    public void setRecurrentRuleDTO(RecurrentRuleDTO recurrentRuleDTO) {
        this.recurrentRuleDTO = recurrentRuleDTO;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }
}
