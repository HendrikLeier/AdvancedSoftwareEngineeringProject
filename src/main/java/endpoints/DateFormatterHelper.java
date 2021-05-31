package endpoints;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatterHelper {

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime parseLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, dateFormatter);
    }

    public static String convertLocalDateTime(LocalDateTime localDateTime) {
        return dateFormatter.format(localDateTime);
    }

}
