package endpoints;


import dto.ParseExceptionDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import parser.generated.CQLParser;
import parser.generated.ParseException;
import parser.helper.CQLParserSingleton;
import parser.querybuilder.*;
import persisted.Event;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("query")
public class QueryController {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ModelMapper modelMapper;

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ParseExceptionDTO> handleParseException(ParseException parseException) {
        return new ResponseEntity<>(new ParseExceptionDTO("Encountered an exception while parsing the query!", parseException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/query")
    public List<List<Object>> query(@RequestBody String query) throws ParseException {
        CQLParser cqlParser = CQLParserSingleton.getCqlParser(new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));

        CriteriaQuery<Tuple> criteriaQuery = entityManager.getCriteriaBuilder().createTupleQuery();
        Root<Event> eventRoot = criteriaQuery.from(Event.class);

        // Reset Ressource Manager and Factory
        ResourceManager resourceManager = new ResourceManager(eventRoot, criteriaQuery, entityManager.getCriteriaBuilder());
        CQLFactory cqlFactory = new CQLFactory(resourceManager);
        cqlParser.setCQLFactory(cqlFactory);
        cqlParser.doParsing();

        TypedQuery<Tuple> eventQuery = entityManager.createQuery(resourceManager.getCriteriaQuery());

        List<Tuple> events = eventQuery.getResultList();
        List<List<Object>> resultTable = new LinkedList<>();

        for (Tuple t : events) {
            List<Object> objects = new LinkedList<>();
            for (int i = 0; i < cqlParser.getResult().getResults().size(); i++) {
                objects.add(t.get(i));
            }
            resultTable.add(objects);
        }

        return resultTable;
    }

}
