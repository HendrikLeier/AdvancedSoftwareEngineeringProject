package parser.querybuilder;

import parser.helper.ArithmeticProdExpression;
import parser.helper.ArithmeticSumExpression;
import parser.helper.ProdType;
import parser.helper.SumType;

import javax.persistence.criteria.Expression;

import java.util.*;

public class ArithmeticSelector {

    public ArithmeticSelector(LogicSelector logicSelector) {
        this.logicSelector = logicSelector;
    }

    private final LogicSelector logicSelector;

    public LogicSelector getLogicSelector() {
        return logicSelector;
    }

    public Expression<Number> sum(Expression<Number> expression, List<ArithmeticSumExpression> arithmeticSumExpressionList){
        Expression<Number> previous = expression;
        for(int i = arithmeticSumExpressionList.size()-1; i >= 0; i--) {
            ArithmeticSumExpression arithmeticSumExpression = arithmeticSumExpressionList.get(i);
            if(arithmeticSumExpression.getSumType() == SumType.ADDITION) {
                previous = logicSelector.getResourceManager().getCriteriaBuilder().sum(previous, arithmeticSumExpression.getExpression());
            } else if(arithmeticSumExpression.getSumType() == SumType.DIFFERENCE) {
                previous = logicSelector.getResourceManager().getCriteriaBuilder().diff(previous, arithmeticSumExpression.getExpression());
            }
        }
        return previous;
    }

    public Expression<Number> prod(Expression<Number> expression, List<ArithmeticProdExpression> arithmeticProdExpressionList) {
        Expression<Number> previous = expression;
        for(int i = arithmeticProdExpressionList.size() - 1; i >= 0; i--) {
            ArithmeticProdExpression arithmeticProdExpression = arithmeticProdExpressionList.get(i);
            if(arithmeticProdExpression.getProdType() == ProdType.PRODUCT) {
                previous = logicSelector.getResourceManager().getCriteriaBuilder().prod(previous, arithmeticProdExpression.getExpression());
            }else if (arithmeticProdExpression.getProdType() == ProdType.QUOTIENT) {
                previous = logicSelector.getResourceManager().getCriteriaBuilder().quot(previous, arithmeticProdExpression.getExpression());
            }
        }
        return previous;
    }

}
