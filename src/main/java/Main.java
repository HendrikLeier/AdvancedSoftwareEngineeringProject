import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persisted.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args)
    {
        logger.debug("starting application....");

        final EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("ASEProjPU");

        Random random = new Random();

        Set<Duration> intervals = new LinkedHashSet<>();

        intervals.add(Duration.ofDays(4));
        intervals.add(Duration.ofHours(5));

        RecurrentRule rule = new RecurrentRule(RecurrentRuleReferencePointType.month, intervals);

        Actor actor = new Actor();
        actor.setName("The deutsche Sparkasse "+ random.nextInt());
        actor.setDescription("Das hier ist die Sparkasse!");

        Event event = new Event();

        event.setEventType(EventType.revenue);
        event.setName("Sold a car");
        event.setLocalDateTime(LocalDateTime.now());
        event.setAmount(15000.0);
        event.setActor(actor);

        RecurrentEvent recurrentEvent = new RecurrentEvent();

        List<Event> eventList = new LinkedList<Event>();
        eventList.add(event);

        recurrentEvent.setType(EventType.revenue);
        recurrentEvent.setEventList(eventList);
        recurrentEvent.setRule(rule);
        recurrentEvent.setAmount(1500.0);
        recurrentEvent.setName("Monthly income");
        recurrentEvent.setStartPoint(LocalDateTime.now());
        recurrentEvent.setEndPoint(LocalDateTime.now());
        recurrentEvent.setActor(actor);

        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            entityManager.persist(actor);
            entityManager.persist(rule);
            entityManager.persist(recurrentEvent);
            entityManager.persist(event);
            tx.commit();
        }
        catch (Exception e) {
            logger.error("cannot commit transaction", e);
            tx.rollback();
        }

        entityManager.close();
        emf.close();
    }

}
