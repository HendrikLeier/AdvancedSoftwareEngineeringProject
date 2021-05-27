package parser.helper;

import parser.generated.ParseException;

import javax.persistence.criteria.Expression;

/**
 * This class has not been generified / typed because JavaCC cannot use the generification / typification anyway...
 */
public class ArithmeticProdExpression {

    public ArithmeticProdExpression(Expression<Number> expression, String token) throws ParseException {
        this.expression = expression;
        if(token.equals("*")) {
            this.prodType = ProdType.PRODUCT;
        }else if(token.equals("/")){
            this.prodType = ProdType.QUOTIENT;
        }else {
            throw new ParseException("Helper in impossible state! Check parser!");
        }
    }

    private final Expression<Number> expression;
    private final ProdType prodType;

    public ProdType getProdType() {
        return prodType;
    }

    public Expression<Number> getExpression() {
        return expression;
    }
}
