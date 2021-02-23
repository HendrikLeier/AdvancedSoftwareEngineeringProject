package repository;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import persisted.Event;
import persisted.RecurrentEvent;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class RecurrentEventCreationRepo {

    @PersistenceContext
    private EntityManager entityManager;

    // Note to myself: Spring takes care of transaction beginning / committing / rolling back...
    @Transactional
    public void persist(RecurrentEvent recurrentEvent) {
        for (Event e : recurrentEvent.getEventList()){
            entityManager.persist(e);
        }

        entityManager.persist(recurrentEvent);
    }

}
