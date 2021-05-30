package unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.generated.ParseException;
import parser.helper.ArithmeticProdExpression;
import parser.helper.ArithmeticSumExpression;
import parser.querybuilder.ArithmeticSelector;
import parser.querybuilder.LogicSelector;
import parser.querybuilder.ResourceManager;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.validation.constraints.NotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

public class TestArithmeticSelector {

    private ArithmeticSelector arithmeticSelector;

    private ResourceManager resourceManager;

    private CriteriaBuilder criteriaBuilder;

    private LogicSelector logicSelector;

    private Expression<Number> sumExpressionAdd;
    private Expression<Number> sumExpressionSub;

    private Expression<Number> prodExpressionMul;
    private Expression<Number> prodExpressionQuot;

    @BeforeEach
    public void prepareArithmeticSelectorTest() {
        logicSelector = mock(LogicSelector.class);
        resourceManager = mock(ResourceManager.class);
        criteriaBuilder = mock(CriteriaBuilder.class);

        sumExpressionAdd = mock(Expression.class);
        sumExpressionSub = mock(Expression.class);
        prodExpressionMul = mock(Expression.class);
        prodExpressionQuot = mock(Expression.class);

        when(logicSelector.getResourceManager()).thenReturn(resourceManager);
        when(resourceManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);

        when(criteriaBuilder.sum(any(Expression.class), any(Expression.class))).thenReturn(sumExpressionAdd);
        when(criteriaBuilder.diff(any(Expression.class), any(Expression.class))).thenReturn(sumExpressionSub);
        when(criteriaBuilder.prod(any(Expression.class), any(Expression.class))).thenReturn(prodExpressionMul);
        when(criteriaBuilder.quot(any(Expression.class), any(Expression.class))).thenReturn(prodExpressionQuot);


    }

    private List<ArithmeticSumExpression> makeArithmeticSumList(int numElements, @NotNull String token) throws ParseException {
        List<ArithmeticSumExpression> arithmeticSumExpressionList = new LinkedList<>();
        for (int i = 0; i < numElements; i++) {
            arithmeticSumExpressionList.add(new ArithmeticSumExpression(mock(Expression.class), token));
        }

        return arithmeticSumExpressionList;
    }

    private List<ArithmeticProdExpression> makeArithmeticProdList(int numElements, @NotNull String token) throws ParseException {
        List<ArithmeticProdExpression> arithmeticSumExpressionList = new LinkedList<>();
        for (int i = 0; i < numElements; i++) {
            arithmeticSumExpressionList.add(new ArithmeticProdExpression(mock(Expression.class), token));
        }
        return arithmeticSumExpressionList;
    }

    @Test
    public void testSelectorSumAdd() throws ParseException {
        arithmeticSelector = new ArithmeticSelector(logicSelector);
        Expression<Number> numberExpression = sumExpressionAdd;
        List<ArithmeticSumExpression> listSumAdd = makeArithmeticSumList(20, "+");
        Expression<Number> result = arithmeticSelector.sum(numberExpression, listSumAdd);

        assert result == sumExpressionAdd;
    }

    @Test
    public void testSelectorSumSub() throws ParseException {
        arithmeticSelector = new ArithmeticSelector(logicSelector);
        Expression<Number> numberExpression = sumExpressionSub;
        List<ArithmeticSumExpression> listSumSub = makeArithmeticSumList(20, "-");
        Expression<Number> result = arithmeticSelector.sum(numberExpression, listSumSub);

        assert result == sumExpressionSub;
    }

    @Test
    public void testSelectorProdMul() throws ParseException {
        arithmeticSelector = new ArithmeticSelector(logicSelector);
        Expression<Number> numberExpression = prodExpressionMul;
        List<ArithmeticProdExpression> listProdMul = makeArithmeticProdList(20, "*");
        Expression<Number> result = arithmeticSelector.prod(numberExpression, listProdMul);

        assert result == prodExpressionMul;
    }

    @Test
    public void testSelectorProdQuot() throws ParseException {
        arithmeticSelector = new ArithmeticSelector(logicSelector);
        Expression<Number> numberExpression = prodExpressionQuot;
        List<ArithmeticProdExpression> listProdQuot = makeArithmeticProdList(20, "/");
        Expression<Number> result = arithmeticSelector.prod(numberExpression, listProdQuot);

        assert result == prodExpressionQuot;
    }

}
