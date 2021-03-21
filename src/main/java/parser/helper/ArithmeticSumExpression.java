package parser.helper;

import parser.generated.ParseException;

import javax.persistence.criteria.Expression;

/**
 * This class has not been generified / typed because JavaCC cannot use the generification / typification anyway...
 */
public class ArithmeticSumExpression {

    public ArithmeticSumExpression(Expression expression, String token) throws ParseException {
        this.expression = expression;
        if(token.equals("+")) {
            this.sumType = SumType.ADDITION;
        }else if(token.equals("-")) {
            this.sumType = SumType.DIFFERENCE;
        }else {
            throw new ParseException("Helper in impossible State! Check parser!");
        }

    }

    private Expression expression;
    private SumType sumType;

    public Expression getExpression() {
        return expression;
    }

    public SumType getSumType() {
        return sumType;
    }
}
