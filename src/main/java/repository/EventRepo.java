package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persisted.Event;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

public class EventRepo {

    private static final Logger logger = LogManager.getLogger(EventRepo.class);

    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EventRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Event> getEventsInInterval(LocalDateTime start, LocalDateTime end) {
        TypedQuery<Event> query = entityManager.createQuery("from Event where localDateTime between :StartDate and :EndDate", Event.class);
        query.setParameter("StartDate", start).setParameter("EndDate", end);

        return query.getResultList();
    }

    public boolean createEvent(Event event) {
        return RepoUtils.transactionEncapsulatedStatement(entityManager, event, (EntityManager::persist), logger);
    }

    public boolean mergeEvent(Event event) {
        return RepoUtils.transactionEncapsulatedStatement(entityManager, event, (EntityManager::merge), logger);
    }

}
