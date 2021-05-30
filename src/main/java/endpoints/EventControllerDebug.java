package endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import persisted.*;
import repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("event/debug")
public class EventControllerDebug {

    @Autowired
    private EventRepo eventRepository;

    @Autowired
    private ActorRepo actorRepo;

    @Autowired
    private RecurrentEventRepo recurrentEventRepo;

    @Autowired
    private RecurrentRuleRepo recurrentRuleRepo;

    @Autowired
    private RecurrentEventCreationRepo recurrentEventCreationRepo;

    @GetMapping("/insertExamples/{count}")
    public Map<String, String> generateExampleEntry(@RequestParam boolean withRecurrentParent, @PathVariable("count") int count) {
        Map<String, String> result = new TreeMap<>();
        Random random = new Random();

        final int numberOfMinutes = 60 * 24 * 3;
        final double amount = 5.00;
        final String name = "Aboné";

        String[] classes = new String[]{"a", "b", "c", "d"};

        List<Actor> actors = actorRepo.findAll();

        Actor actor;

        if (actors.size() == 0) {
            Actor a = new Actor();
            a.setName("Hendrik's Example Actor");
            a.setDescription("This is an example Description\nHave fun!");
            actor = actorRepo.save(a);
            result.put("actor", "generated");
        }else {
            actor = actors.get(0);
            result.put("actor", "loaded");
        }

        RecurrentEvent recurrentEvent = null;

        Duration increase = Duration.ofMinutes(numberOfMinutes / count);

        if(withRecurrentParent) {
            result.put("parent", "yes");
            recurrentEvent = new RecurrentEvent();
            recurrentEvent.setActor(actor);
            recurrentEvent.setStartPoint(LocalDateTime.now().minus(Duration.ofMinutes(numberOfMinutes)));
            recurrentEvent.setType(EventType.expenditure);
            recurrentEvent.setAmount(amount + random.nextDouble() * 3);
            recurrentEvent.setName(classes[random.nextInt(classes.length)]);
            recurrentEvent.setEndPoint(LocalDateTime.now());


            RecurrentRule rule = new RecurrentRule(increase);

            recurrentEvent.setRule(rule);

            recurrentRuleRepo.save(rule);
        }else {
            result.put("parent", "no");
        }

        Set<Event> events = new LinkedHashSet<>();

        LocalDateTime current;

        if (withRecurrentParent)
            current = recurrentEvent.getStartPoint();
        else
            current = LocalDateTime.now().minus(Duration.ofMinutes(numberOfMinutes));

        for (int i = 0; i < count; i++) {
            Event event = new Event();

            event.setActor(actor);
            event.setEventType(EventType.expenditure);
            event.setAmount(amount + random.nextDouble() * 3);
            event.setName(classes[random.nextInt(classes.length)]);
            event.setLocalDateTime(current);
            if(withRecurrentParent) { event.setRecurrentParent(recurrentEvent); }
            events.add(event);

            current = current.plus(increase);
        }

        result.put("events", ""+count);

        if (withRecurrentParent) {
            recurrentEvent.setEventList(events);
            recurrentEventCreationRepo.persist(recurrentEvent);
        }else {
            eventRepository.saveAll(events);
        }

        result.put("status", "ok");

        return result;
    }

}
