package dto;

import persisted.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventDTOLean {

    private UUID eventId;

    private String name;

    private String localDateTime;

    private EventType type;

    private double amount;

    private UUID actor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
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

    public UUID getActor() {
        return actor;
    }

    public void setActor(UUID actor) {
        this.actor = actor;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
}
