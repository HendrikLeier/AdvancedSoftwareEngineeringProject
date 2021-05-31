package endpoints;

import dto.RecurrentEventDTO;
import dto.RecurrentEventDTOLean;
import dto.RecurrentRuleDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import persisted.*;
import repository.ActorRepo;
import repository.RecurrentEventUnificationRepo;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("recurrentEvent")
public class RecurrentEventController {

    @Autowired
    private RecurrentEventUnificationRepo recurrentEventRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ActorRepo actorRepo;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDateTime parseLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, dateFormatter);
    }

    @PostMapping("/insert")
    public ResponseEntity<UUID> generateRecurrentEvent(@RequestBody RecurrentEventDTOLean recurrentEventDTOLean) throws EndpointException {
        Optional<Actor> actorOptional = actorRepo.findById(recurrentEventDTOLean.getActorId());
        if(actorOptional.isEmpty()) {
            throw new EndpointException("Actor not found!");
        }

        Actor actor = actorOptional.get();

        RecurrentEvent recurrentEvent = new RecurrentEvent();
        recurrentEvent.setActor(actor);
        recurrentEvent.setStartPoint(parseLocalDateTime(recurrentEventDTOLean.getStartPoint()));
        recurrentEvent.setType(recurrentEventDTOLean.getType());
        recurrentEvent.setAmount(recurrentEventDTOLean.getAmount());
        recurrentEvent.setName(recurrentEventDTOLean.getName());
        recurrentEvent.setEndPoint(parseLocalDateTime(recurrentEventDTOLean.getEndPoint()));

        RecurrentRuleDTO recurrentRuleDTO = recurrentEventDTOLean.getRecurrentRuleDTO();
        Duration ruleInterval = Duration.parse(recurrentRuleDTO.getInterval());

        RecurrentRule recurrentRule = null;

        if(recurrentRuleDTO.getType() == RecurrentRuleType.beginBased) {
            recurrentRule = new RecurrentRule(recurrentRuleDTO.getReferencePointType(), ruleInterval);
        }else if(recurrentRuleDTO.getType() == RecurrentRuleType.interval) {
            recurrentRule = new RecurrentRule(ruleInterval);
        } else {
            throw new RuntimeException("No rule type specified");
        }

        recurrentEvent.setRule(recurrentRule);
        recurrentEvent.setEventList(new HashSet<>());
        recurrentEventRepo.save(recurrentEvent);

        return new ResponseEntity<>(recurrentEvent.getRecurrentEventId(), HttpStatus.OK);
    }

    @GetMapping("/find/{uuid}")
    public ResponseEntity<RecurrentEventDTO> getSingle(@PathVariable("uuid") UUID uuid) {
        RecurrentEvent recurrentEvent;
        try {
            recurrentEvent = recurrentEventRepo.getOne(uuid);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(modelMapper.map(recurrentEvent, RecurrentEventDTO.class), HttpStatus.OK);
    }

    @PutMapping("/update/{uuid}")
    public ResponseEntity<HttpStatus> updateEvent(@PathVariable("uuid") UUID uuid) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<HttpStatus> deleteRecurrentEvent(@PathVariable("uuid") UUID uuid) {
        RecurrentEvent recurrentEvent;
        try {
            recurrentEvent = recurrentEventRepo.getOne(uuid);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        recurrentEventRepo.delete(recurrentEvent);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
