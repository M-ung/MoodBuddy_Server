package moodbuddy.moodbuddy.global.util;

import java.time.LocalDate;
import java.time.YearMonth;

public class DateUtil {
    public static String formatYear(LocalDate date) {
        return String.valueOf(date.getYear());
    }

    public static String formatMonth(LocalDate date) {
        return String.format("%02d", date.getMonthValue());
    }

    public static LocalDate[] getLastMonthDates() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        return new LocalDate[]{lastMonth.atDay(1), lastMonth.atEndOfMonth()};
    }
}