package persisted;

import javax.persistence.*;
import javax.ws.rs.DefaultValue;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Event {

    public Event() {
        this.eventId = UUID.randomUUID();
    }

    @Id
    private final UUID eventId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @Enumerated(EnumType.STRING)
    private EventType type;

    private double amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Actor actor;

    public Boolean getSomeTestField() {
        return someTestField;
    }

    public void setSomeTestField(Boolean someTestField) {
        this.someTestField = someTestField;
    }

    @Column(columnDefinition = "boolean default false")
    private Boolean someTestField;

    @ManyToOne(fetch = FetchType.LAZY)
    private RecurrentEvent recurrentParent;

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

    public EventType getEventType() {
        return type;
    }

    public void setEventType(EventType eventType) {
        this.type = eventType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public UUID getEventId() {
        return eventId;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public RecurrentEvent getRecurrentParent() {
        return recurrentParent;
    }

    public void setRecurrentParent(RecurrentEvent recurrentParent) {
        this.recurrentParent = recurrentParent;
    }
}
