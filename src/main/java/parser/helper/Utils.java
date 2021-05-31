package parser.helper;

import parser.generated.ParseException;
import parser.querybuilder.LogicSelector;
import parser.querybuilder.Order;
import parser.querybuilder.OrderType;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

public class Utils {


    private static DateTimeFormatter dateTimeFormatter;
    
    public static void convertEscapedCharacter(StringBuilder builder, char c) {
        switch (c) {
            case 'b': builder.append('\b'); break;
            case 'f': builder.append('\f'); break;
            case 'n': builder.append('\n'); break;
            case 'r': builder.append('\r'); break;
            case 't': builder.append('\t'); break;
            default: builder.append(c);
        }
    }

    public static Predicate processNumericComparison(Expression<Number> value1, String comparisonOperator, Expression<Number> value2, LogicSelector filter) throws ParseException {
        switch (comparisonOperator) {
            case "=": return filter.handleEqualObj(value1, value2);
            case ">": return filter.handleGreaterObj(value1, value2, false);
            case "<": return filter.handleSmallerObj(value1, value2, false);
            case ">=": return filter.handleGreaterObj(value1, value2, true);
            case "<=": return filter.handleSmallerObj(value1, value2, true);
            default: throw new ParseException("Numeric comparison in unreachable state, check parser!");
        }
    }

    public static Predicate processStringComparison(Expression<String> value1, String comparisonOperator, Expression<String> value2, LogicSelector filter) throws ParseException {
        switch (comparisonOperator) {
            case "=": return filter.handleEqualObj(value1, value2);
            case "like": return filter.handleLike(value1, value2);
            case "startswith": return filter.handleStartswith(value1, value2);
            case "endswith": return filter.handleEndswith(value1, value2);
            case "contains": return filter.handleContains(value1, value2);
            default: throw new ParseException("String comparison in unreachable state, check parser!");
        }
    }

    public static Predicate processDateComparison(Expression<LocalDateTime> value1, String comparisonOperator, Expression<LocalDateTime> value2, LogicSelector filter) throws ParseException {
        switch (comparisonOperator) {
            case "=": return filter.handleEqualObj(value1, value2);
            case ">": return filter.handleGreaterObj(value1, value2, false);
            case "<": return filter.handleSmallerObj(value1, value2, false);
            case ">=": return filter.handleGreaterObj(value1, value2, true);
            case "<=": return filter.handleSmallerObj(value1, value2, true);
            default: throw new ParseException("Date comparison in unreachable state, check parser!");
        }
    }

    public static Predicate processUUIDComparison(Expression<UUID> value1, String comparisonOperator, Expression<UUID> value2, LogicSelector filter) throws ParseException {
        switch (comparisonOperator) {
            case "=": return filter.handleEqualObj(value1, value2);
            case ">": return filter.handleGreaterObj(value1, value2, false);
            case "<": return filter.handleSmallerObj(value1, value2, false);
            case ">=": return filter.handleGreaterObj(value1, value2, true);
            case "<=": return filter.handleSmallerObj(value1, value2, true);
            default: throw new ParseException("Date comparison in unreachable state, check parser!");
        }
    }

    public static Predicate processBinaryComparison(Expression value1, String comparisonOperator, Expression value2, LogicSelector filter) throws ParseException {
        switch (comparisonOperator) {
            case "=": return filter.handleEqualObj(value1, value2);
            case "<": return filter.handleSmallerObj(value1, value2, false);
            case "<=": return filter.handleSmallerObj(value1, value2, true);
            case "like": return filter.handleLike(value1, value2);
            case "startswith": return filter.handleStartswith(value1, value2);
            case "endswith": return filter.handleEndswith(value1, value2);
            case "contains": return filter.handleContains(value1, value2);
            default: throw new ParseException("Comparison in unreachable state, check parser!");
        }
    }

    public static Predicate processTernaryComparison(Expression value1, String comparisonOperator, Expression value2, Expression value3, LogicSelector filter) throws ParseException {
        switch (comparisonOperator) {
            case "between": return filter.handleBetween(value1, value2, value3);
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

    public static void processOrder(Expression<?> field, String direction, Order order) throws ParseException {
        switch (direction) {
            case "ascending": order.addOrder(field, OrderType.ASCENDING); break;
            case "descending": order.addOrder(field, OrderType.DESCENDING); break;
            default: throw new ParseException("Order in unreachable state, check parser!");
        }
    }
}
