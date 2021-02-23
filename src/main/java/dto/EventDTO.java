package dto;

import persisted.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventDTO {

    private UUID eventId;

    private String name;

    private LocalDateTime localDateTime;

    private EventType type;

    private double amount;

    private ActorDTO actor;

    private RecurrentEventDTO recurrentParent;

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
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

    public RecurrentEventDTO getRecurrentParent() {
        return recurrentParent;
    }

    public void setRecurrentParent(RecurrentEventDTO recurrentParent) {
        this.recurrentParent = recurrentParent;
    }
}
