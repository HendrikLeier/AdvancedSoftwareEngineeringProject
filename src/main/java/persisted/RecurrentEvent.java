package persisted;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class RecurrentEvent {

    public RecurrentEvent() {
        this.recurrentEventId = UUID.randomUUID();
    }

    @Id
    private final UUID recurrentEventId;

    @OneToOne
    private RecurrentRule rule;

    @Column(nullable = false)
    private LocalDateTime startPoint;

    @Column(nullable = false)
    private LocalDateTime endPoint;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(nullable = false)
    private String name;

    private double amount;

    @ManyToOne(optional = false)
    private Actor actor;

    @OneToMany
    @JoinColumn
    private List<Event> eventList;

    public RecurrentRule getRule() {
        return rule;
    }

    public void setRule(RecurrentRule rule) {
        this.rule = rule;
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

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public UUID getRecurrentEventId() {
        return recurrentEventId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }
}
