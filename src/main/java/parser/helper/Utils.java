package parser.helper;

import parser.generated.ParseException;
import parser.querybuilder.FieldException;
import parser.querybuilder.Filter;
import parser.querybuilder.Order;
import parser.querybuilder.OrderType;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

public class Utils {

    public static void printList(List list) {
        list.forEach(System.out::println);
    }

    private static DateTimeFormatter dateTimeFormatter;

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

    public static Predicate processBinaryComparison(String fieldName, String comparisonOperator, Object value, Filter filter) throws ParseException {
        switch (comparisonOperator) {
            case "=": return filter.handleEqualObj(fieldName, value);
            case ">": return filter.handleGreaterObj(fieldName, value, false);
            case "<": return filter.handleSmallerObj(fieldName, value, false);
            case ">=": return filter.handleGreaterObj(fieldName, value, true);
            case "<=": return filter.handleSmallerObj(fieldName, value, true);
            case "like": return filter.handleLike(fieldName, value);
            case "startswith": return filter.handleStartswith(fieldName, value);
            case "endswith": return filter.handleEndswith(fieldName, value);
            case "contains": return filter.handleContains(fieldName, value);
            default: throw new ParseException("Comparison in unreachable state, check parser!");
        }
    }

    public static Predicate processTernaryComparison(String fieldName, String comparisonOperator, Object value1, Object value2, Filter filter) throws ParseException {
        switch (comparisonOperator) {
            case "between": return filter.handleBetween(fieldName, value1, value2);
            default: throw new ParseException("Comparison in unreachable state, check parser!");
        }
    }

    public static LocalDateTime parseDateTime(String date) throws DateTimeParseException {
        if(dateTimeFormatter == null) {
            dateTimeFormatter = getDatetimeFormatter();
        }
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    private static DateTimeFormatter getDatetimeFormatter() {
        return new DateTimeFormatterBuilder()
                .appendPattern("dd.MM.yyyy[ HH:mm]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }

    public static void processOrder(String fieldName, String direction, Order order) throws ParseException {
        switch (direction) {
            case "ascending": order.addOrder(fieldName, OrderType.ASCENDING); break;
            case "descending": order.addOrder(fieldName, OrderType.DESCENDING); break;
            default: throw new ParseException("Order in unreachable state, check parser!");
        }
    }
}
