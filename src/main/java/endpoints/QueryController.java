package endpoints;


import dto.EventDTO;
import org.aspectj.weaver.ast.Or;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import parser.generated.CQLParser;
import parser.generated.ParseException;
import parser.querybuilder.Filter;
import parser.querybuilder.Order;
import parser.querybuilder.ResourceManager;
import persisted.Event;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("query")
public class QueryController {



    @Autowired
    private EntityManager entityManager;

    private boolean init = false;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/testQuery")
    public List<EventDTO> testQuery(@RequestBody String query) throws ParseException {
        if (!init) {
            new CQLParser(new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
            init = true;
        } else {
            CQLParser.ReInit(new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
        }

        CriteriaQuery<Event> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);

        ResourceManager resourceManager = new ResourceManager(eventRoot, criteriaQuery, entityManager.getCriteriaBuilder());

        Filter filter = new Filter(resourceManager);

        Order order = new Order(resourceManager);

        Predicate predicate = CQLParser.Filter(filter);
        CQLParser.Order(order);

        resourceManager.finalizeQuery(predicate);
        order.finalizeOrder();

        TypedQuery<Event> eventQuery = entityManager.createQuery(resourceManager.getCriteriaQuery());

        List<Event> events = eventQuery.getResultList();

        return events.stream().map(e -> modelMapper.map(e, EventDTO.class)).collect(Collectors.toList());
    }

}
