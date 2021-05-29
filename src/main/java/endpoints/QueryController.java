package endpoints;


import dto.ParseExceptionDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import parser.generated.CQLParser;
import parser.generated.ParseException;
import parser.querybuilder.*;
import persisted.Event;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Link;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("query")
public class QueryController {

    @Autowired
    private EntityManager entityManager;

    private boolean init = false;

    @Autowired
    private ModelMapper modelMapper;

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ParseExceptionDTO> handleParseException(ParseException parseException) {
        return new ResponseEntity<>(new ParseExceptionDTO("Encountered an exception while parsing the query!", parseException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/query")
    public List<List<Object>> testQuery(@RequestBody String query) throws ParseException {
        if (!init) {
            new CQLParser(new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
            init = true;
        } else {
            CQLParser.ReInit(new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
        }

        CriteriaQuery<Tuple> criteriaQuery = entityManager.getCriteriaBuilder().createTupleQuery();
        Root<Event> eventRoot = criteriaQuery.from(Event.class);

        ResourceManager resourceManager = new ResourceManager(eventRoot, criteriaQuery, entityManager.getCriteriaBuilder());

        FilterSelector filter = new FilterSelector(resourceManager);

        Order order = new Order(resourceManager);

        Result result = new Result(resourceManager);

        ResultField resultField = new ResultField(resourceManager);

        Group group = new Group(resourceManager);

        CQLParser.resourceManager = resourceManager;

        CQLParser.Result(result, resultField);
        CQLParser.Filter(filter);
        CQLParser.Order(order);
        CQLParser.Group(group);
        CQLParser.Timebin(group, result, order);

        result.finalizeResult();
        filter.finalizeSelector();
        order.finalizeOrder();
        group.finalizeGroup();

        TypedQuery<Tuple> eventQuery = entityManager.createQuery(resourceManager.getCriteriaQuery());

        List<Tuple> events = eventQuery.getResultList();

        List<List<Object>> resultTable = new LinkedList<>();

        for (Tuple t : events) {
            List<Object> objects = new LinkedList<>();
            for (int i = 0; i < result.getResults().size(); i++) {
                objects.add(t.get(i));
            }
            resultTable.add(objects);
        }

        return resultTable;
    }

}
