package unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import parser.generated.ParseException;
import parser.querybuilder.FieldException;
import parser.querybuilder.LogicOperator;
import parser.querybuilder.LogicSelector;
import parser.querybuilder.ResourceManager;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

public class TestLogicSelector {

    private LogicSelector logicSelector;

    private ResourceManager resourceManager;

    private Predicate predicate1;
    private Predicate predicate2;

    @BeforeEach
    public void prepareLogicSelector() {
        resourceManager = mock(ResourceManager.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        /*Initialize cheapest version of abstract class */
        logicSelector = new LogicSelector(resourceManager) {
            @Override
            public void finalizeSelector() {}

            @Override
            public <X> Expression<X> getReferencedFieldOfType(String fieldName, Class<X> type) throws FieldException {return null;}

            @Override
            public <Y> Expression<Number> getAggregateOf(String aggregateName, Expression<Y> field) throws FieldException, ParseException {return null;}
        };

        predicate1 = mock(Predicate.class);
        predicate2 = mock(Predicate.class);

        when(resourceManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);

        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate1);
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate2);
    }

    private List<Predicate> getNPredicates(int numberOfPredicates) {
        List<Predicate> myPredicates = new LinkedList<>();

        for (int i = 0; i < numberOfPredicates; i++) {
            myPredicates.add(predicate1);
        }
        return myPredicates;
    }

    @Test
    @DisplayName("test for handlePredicateList for and")
    public void testHandlePredicateListAnd() {
        List<Predicate> predicates = getNPredicates(100);

        Predicate result = logicSelector.handlePredicateList(predicates, LogicOperator.And);

        assert result == predicate1;
    }

    @Test
    @DisplayName("test for handlePredicateList for or")
    public void testHandlePredicateListOr() {
        List<Predicate> predicates = getNPredicates(100);

        Predicate result = logicSelector.handlePredicateList(predicates, LogicOperator.Or);

        assert result == predicate2;
    }

}
