package dto;

import persisted.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecurrentEventDTO {
    private UUID recurrentEventId;

    private LocalDateTime startPoint;

    private LocalDateTime endPoint;

    private EventType type;

    private String name;

    private double amount;

    private ActorDTO actor;

    public UUID getRecurrentEventId() {
        return recurrentEventId;
    }

    public void setRecurrentEventId(UUID recurrentEventId) {
        this.recurrentEventId = recurrentEventId;
    }

    public LocalDateTime getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LocalDateTime startPoint) {
        this.startPoint = startPoint;
    }

    public LocalDateTime getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LocalDateTime endPoint) {
        this.endPoint = endPoint;
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

    public ActorDTO getActor() {
        return actor;
    }

    public void setActor(ActorDTO actor) {
        this.actor = actor;
    }
}
