package endpoints;

import dto.RecurrentEventDTO;
import dto.RecurrentEventDTOLean;
import dto.RecurrentRuleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import persisted.*;
import repository.ActorRepo;
import repository.RecurrentEventRepo;
import repository.RecurrentRuleRepo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("recurrentEvent")
public class RecurrentEventController {

    @Autowired
    private RecurrentEventRepo recurrentEventRepo;

    @Autowired
    private RecurrentRuleRepo recurrentRuleRepo;

    @Autowired
    private ActorRepo actorRepo;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDateTime parseLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, dateFormatter);
    }

    @PostMapping("/insert")
    public ResponseEntity<HttpStatus> generateRecurrentEvent(@RequestBody RecurrentEventDTOLean recurrentEventDTOLean) throws EndpointException {
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

        recurrentRuleRepo.save(recurrentRule);

        recurrentEventRepo.save(recurrentEvent);

        return ResponseEntity.ok(HttpStatus.OK);
    }

}
