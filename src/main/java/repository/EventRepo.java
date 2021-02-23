package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import persisted.Event;

import java.util.UUID;

@Repository
public interface EventRepo extends JpaRepository<Event, UUID> {
}
