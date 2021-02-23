package endpoints;

import dto.EventDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import persisted.Event;
import repository.EventRepo;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("event")
public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/findall")
    public List<EventDTO> findAll() {
        List<Event> events = eventRepo.findAll();

        return events.stream().map(e -> modelMapper.map(e, EventDTO.class)).collect(Collectors.toList());
    }

}
