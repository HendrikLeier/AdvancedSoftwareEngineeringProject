package parser.helper;

import parser.generated.ParseException;
import parser.querybuilder.FieldException;
import parser.querybuilder.Filter;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Utils {

    public static void printList(List list) {
        list.forEach(System.out::println);
    }

    public static Expr handleExprList(List exprList, String val) {
        Expr result = new Expr();
        Expr currExpr = result;
        if (exprList.size() > 1) {
            for(int i = 0; i < exprList.size() - 1 ; i++) {
                currExpr.right = (Expr) exprList.get(i);
                currExpr.val = val;
                if (i != exprList.size() - 2) {
                    currExpr.left = new Expr();
                    currExpr = currExpr.left;
                }
            }
            currExpr.left = (Expr) exprList.get(exprList.size() - 1);

            return result;
        } else {
            return (Expr) exprList.get(0);
        }
    }



    public static void convertEscapedCharacter(StringBuilder builder, char c) {
        switch (c) {
            case 'b': builder.append((char) '\b'); break;
            case 'f': builder.append((char) '\f'); break;
            case 'n': builder.append((char) '\n'); break;
            case 'r': builder.append((char) '\r'); break;
            case 't': builder.append((char) '\t'); break;
            default: builder.append(c);
        }
    }

    public static Predicate processComparison(String fieldName, String comparisonOperator, Object value, Filter filter) throws ParseException {
        switch (comparisonOperator) {
            case "=": return filter.handleEqualObj(fieldName, value);

            default: throw new ParseException("Comparison in unreachable state, check parser!");
        }
    }

    public static LocalDateTime parseDateTime(String date) throws ParseException, DateTimeParseException {
        if(date.length() == 10) {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } else if(date.length() == 16) {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } else throw new ParseException("Could not Parse this date, as its length was wrong");
    }
}
