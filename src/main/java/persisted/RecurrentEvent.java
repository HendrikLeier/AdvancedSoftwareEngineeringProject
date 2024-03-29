package persisted;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
public class RecurrentEvent {

    public RecurrentEvent() {
        this.recurrentEventId = UUID.randomUUID();
    }

    @Id
    private final UUID recurrentEventId;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RecurrentRule rule;

    @Column(nullable = false)
    private LocalDateTime startPoint;

    @Column(nullable = false)
    private LocalDateTime endPoint;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(nullable = false)
    private String name;

    private Double amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Actor actor;

    @OneToMany(mappedBy = "recurrentParent", fetch = FetchType.LAZY)
    private Set<Event> eventList;

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

    public Set<Event> getEventList() {
        return eventList;
    }

    public void setEventList(Set<Event> eventList) {
        this.eventList = eventList;

        // Bidirectional relationship...
        for (Event event : eventList) {
            event.setRecurrentParent(this);
        }
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
