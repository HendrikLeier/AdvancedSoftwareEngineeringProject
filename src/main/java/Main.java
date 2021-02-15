import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persisted.Event;
import persisted.EventType;
import persisted.RecurrentEvent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args)
    {
        logger.debug("starting application....");

        final EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("ASEProjPU");

        Event event = new Event();

        event.setEventType(EventType.revenue);
        event.setName("Sold a car");
        event.setLocalDateTime(LocalDateTime.now());
        event.setAmount(15000.0);

        RecurrentEvent recurrentEvent = new RecurrentEvent();

        List<Event> eventList = new LinkedList<Event>();
        eventList.add(event);

        recurrentEvent.setType(EventType.revenue);
        recurrentEvent.setEventList(eventList);
        recurrentEvent.setDurationMillis(123456789L);
        recurrentEvent.setAmount(1500.0);
        recurrentEvent.setName("Monthly income");
        recurrentEvent.setStartPoint(LocalDateTime.now());
        recurrentEvent.setEndPoint(LocalDateTime.now());

        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            entityManager.persist(recurrentEvent);
            entityManager.persist(event);
            tx.commit();
        }
        catch (Exception e) {
            logger.error("cannot commit transaction", e);
            tx.rollback();
        }

        Event event1 = entityManager.find(Event.class, UUID.fromString("c0359805-be95-4f66-b111-3d28f41520f1"));

        logger.info("Retrieved Event from " + event1.getLocalDateTime());

        entityManager.close();
        emf.close();
    }

}
