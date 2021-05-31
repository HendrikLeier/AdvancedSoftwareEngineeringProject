package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import persisted.RecurrentEvent;

import java.util.UUID;

@Repository
interface RecurrentEventRepo extends JpaRepository<RecurrentEvent, UUID> {
}
