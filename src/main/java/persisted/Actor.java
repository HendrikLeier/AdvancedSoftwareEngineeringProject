package persisted;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
public class Actor {

    public Actor() {
        this.actorId = UUID.randomUUID();
    }

    @Id
    private final UUID actorId;

    @Column(unique = true)
    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
