package endpoints;

import dto.ActorDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import persisted.Actor;
import repository.ActorRepo;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("actor")
public class ActorController {

    @Autowired
    private ActorRepo actorRepo;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/insert")
    public ResponseEntity<UUID> generateActor(@RequestBody ActorDTO actorDTO) {
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setDescription(actorDTO.getDescription());

        actorRepo.save(actor);

        return new ResponseEntity<>(actor.getActorId(), HttpStatus.OK);
    }

    @GetMapping("/findall")
    public List<ActorDTO> getAllActors() {
        List<Actor> actors = actorRepo.findAll();

        return actors.stream().map(a -> modelMapper.map(a, ActorDTO.class)).collect(Collectors.toList());
    }

    @PutMapping("/update")
    public ResponseEntity<HttpStatus> updateActor(@RequestBody ActorDTO actorDTO) {
        Actor actor;
        try {
            actor = actorRepo.getOne(actorDTO.getActorId());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        actor.setName(actorDTO.getName());
        actor.setDescription(actorDTO.getDescription());

        actorRepo.save(actor);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{uuid}")
    public ResponseEntity<ActorDTO> getSingle(@PathVariable("uuid") UUID uuid) {
        Actor actor;
        try {
            actor = actorRepo.getOne(uuid);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(modelMapper.map(actor, ActorDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<HttpStatus> deleteActor(@PathVariable("uuid") UUID uuid) {
        Actor actor;
        try {
            actor = actorRepo.getOne(uuid);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        actorRepo.delete(actor);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
