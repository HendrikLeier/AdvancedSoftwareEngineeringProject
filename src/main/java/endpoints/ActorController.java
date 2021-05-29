package endpoints;

import dto.ActorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import persisted.Actor;
import repository.ActorRepo;

import java.util.UUID;

@RestController
@RequestMapping("actor")
public class ActorController {

    @Autowired
    private ActorRepo actorRepo;

    @PostMapping("/insert")
    public ResponseEntity<UUID> generateActor(@RequestBody ActorDTO actorDTO) {
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setDescription(actorDTO.getDescription());

        actorRepo.save(actor);

        return new ResponseEntity<>(actor.getActorId(), HttpStatus.OK);
    }

}
