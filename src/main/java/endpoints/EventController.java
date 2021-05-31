package endpoints;

import dto.EventDTO;
import dto.EventDTOLean;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import persisted.Actor;
import persisted.Event;
import repository.ActorRepo;
import repository.EventRepo;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("event")
public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private ActorRepo actorRepo;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/findall")
    public List<EventDTO> findAll() {
        List<Event> events = eventRepo.findAll();

        return events.stream().map(e -> modelMapper.map(e, EventDTO.class)).collect(Collectors.toList());
    }

    @PostMapping("/insert")
    public ResponseEntity<UUID> generateEvent(@RequestBody EventDTOLean eventDTO) {
        Actor actor = actorRepo.getOne(eventDTO.getActor());

        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setAmount(eventDTO.getAmount());
        event.setActor(actor);
        event.setEventType(eventDTO.getType());
        event.setRecurrentParent(null);
        event.setLocalDateTime(DateFormatterHelper.parseLocalDateTime(eventDTO.getLocalDateTime()));

        eventRepo.save(event);

        return new ResponseEntity<>(event.getEventId(),HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<HttpStatus> updateEvent(@RequestBody EventDTOLean eventDTO) {
        Event event;
        try {
            event = eventRepo.getOne(eventDTO.getEventId());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Actor actor = event.getActor();
        event.setName(eventDTO.getName());
        event.setAmount(eventDTO.getAmount());
        event.setActor(actor);
        event.setEventType(eventDTO.getType());
        event.setRecurrentParent(null);
        event.setLocalDateTime(DateFormatterHelper.parseLocalDateTime(eventDTO.getLocalDateTime()));

        eventRepo.save(event);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<HttpStatus> deleteEvent(@PathVariable("uuid") UUID uuid) {
        Event event;
        try {
            event = eventRepo.getOne(uuid);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        eventRepo.delete(event);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{uuid}")
    public ResponseEntity<EventDTO> getSingle(@PathVariable("uuid") UUID uuid) {
        Event event;
        try {
            event = eventRepo.getOne(uuid);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(modelMapper.map(event,EventDTO.class), HttpStatus.OK);
    }

}
