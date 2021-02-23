package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import persisted.RecurrentRule;

import java.util.UUID;

@Repository
public interface RecurrentRuleRepo extends JpaRepository<RecurrentRule, UUID> {
}
