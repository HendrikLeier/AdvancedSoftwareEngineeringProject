package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import persisted.Actor;

import java.util.UUID;

@Repository
public interface ActorRepo extends JpaRepository<Actor, UUID> {
}
