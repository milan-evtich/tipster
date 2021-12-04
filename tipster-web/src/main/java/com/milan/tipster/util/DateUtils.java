package com.milan.tipster.util;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.commons.nullanalysis.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DateUtils {

    public static final String LOCAL_DATE_PATTERN_DEFAULT = "dd.MM.yyyy";
    public static final String LOCAL_TIME_PATTERN_DEFAULT = "hh:mm";

    public static String format(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return formatLocalDateWithoutZ(localDate.format(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN_DEFAULT)));
    }

    public static String format(LocalDate localDate, DateTimeFormatter formatter) {
        if (localDate == null) {
            return null;
        }

        return formatLocalDateWithoutZ(localDate.format(formatter));
    }

    public static LocalDate parse(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        return LocalDate.parse(date, DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN_DEFAULT));
    }

    public static LocalDate parse(String date, DateTimeFormatter formatter) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        return LocalDate.parse(date, formatter);
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return DAYS.between(start, end.plusDays(1));
    }

    /**
     * Возвращает рабочий день, если {@param date} суббота или воскресение, то возвращает понедельник
     * В остальных случаях возвращает {@param date} без изменении
     */
    public static LocalDate getFirstWorkingDay(@NotNull LocalDate date) {
        requireNonNull(date);

        switch (date.getDayOfWeek()) {
            case SATURDAY:
                return date.plusDays(2);
            case SUNDAY:
                return date.plusDays(1);
            default:
                return date;
        }
    }

    public static String formatLocalDateWithoutZ(String date){
        if (isNotBlank(date) && endsWith(date, "Z")){
            return date.replace("Z", "");
        }
        return date;
    }

    public static long hoursBetween(OffsetDateTime start, OffsetDateTime end) {
        return HOURS.between(start, end);
    }
}
