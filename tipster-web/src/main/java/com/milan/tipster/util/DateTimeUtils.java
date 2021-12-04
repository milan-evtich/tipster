package com.milan.tipster.util;

import org.codehaus.commons.nullanalysis.NotNull;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DateTimeUtils {

    public static final String LOCAL_DATE_TIME_PATTERN_DEFAULT = "dd.MM.yyyy HH:mm:ss";
    public static final String OFFSET_DATE_TIME_PATTERN_DEFAULT = "yyyy-MM-dd'T'HH:mmZZZZZ";
    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm МСК";
    public static final long START_OF_DAY = 0;
    public static final long END_OF_DAY = 24;
    private static Predicate<? super LocalDate> isWeekend = date -> date.getDayOfWeek().equals(DayOfWeek.SATURDAY)
            || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);

    public static String format(LocalDateTime localDateTime) {
        if (isNull(localDateTime)) {
            return null;
        }

        return formatTimeZoneWithoutZ(localDateTime.format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN_DEFAULT)));
    }

    public static String format(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        if (isNull(localDateTime)) {
            return null;
        }

        return formatTimeZoneWithoutZ(localDateTime.format(formatter));
    }

    public static LocalDateTime parse(String dateTime) {
        if (isBlank(dateTime)) {
            return null;
        }

        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN_DEFAULT));
    }

    public static String formatDuration(Duration duration) {
        if (isNull(duration)) {
            return null;
        }

        return formatTimeZoneWithoutZ(duration.toString());
    }

    public static Duration parseDuration(String time) {
        if (isBlank(time)) {
            return null;
        }

        return Duration.parse(time);
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static LocalDateTime parse(String dateTime, DateTimeFormatter formatter) {
        if (isBlank(dateTime)) {
            return null;
        }

        return LocalDateTime.parse(dateTime, formatter);
    }

    public static LocalDateTime startOfToday(Clock clock) {
        return LocalDateTime.of(LocalDate.now(clock), LocalTime.MIN);
    }

    public static OffsetDateTime convertLocalDateToOffsetDateTime(@NotNull LocalDate localDate, int hour, int minute, String timeZone) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(hour, minute));
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(timeZone));
        ZoneOffset zoneOffset = zonedDateTime.getOffset();
        return OffsetDateTime.of(localDateTime, zoneOffset);
    }

    public static OffsetDateTime convertLocalDateTimeToServerTimeZone(LocalDate localDate, LocalTime localTime, ZoneOffset localZoneOffset){
        OffsetDateTime convertedDateTime = OffsetDateTime.of(localDate, localTime, localZoneOffset);
        convertedDateTime = convertOffsetDateTimeToTimeWithTimeZone(convertedDateTime, ZonedDateTime.now().getZone());
        return convertedDateTime;
    }

    public static OffsetDateTime convertOffsetDateTimeToTimeWithTimeZone(OffsetDateTime offsetDateTime, ZoneId zoneId){
        return OffsetDateTime.ofInstant(Instant.from(offsetDateTime), zoneId);
    }

    public static String format(OffsetDateTime offsetDateTime) {
        if (isNull(offsetDateTime)) {
            return null;
        }
        return formatTimeZoneWithoutZ(offsetDateTime.format(DateTimeFormatter.ofPattern(OFFSET_DATE_TIME_PATTERN_DEFAULT)));
    }

    public static String formatToOffsetDateWithTimezone(OffsetDateTime offsetDateTime) {
        if (isNull(offsetDateTime)) {
            return null;
        }
        return formatTimeZoneWithoutZ(offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE));
    }

    public static String formatToOffsetTimeWithTimezone(OffsetDateTime offsetDateTime) {
        if (isNull(offsetDateTime)) {
            return null;
        }
        return formatTimeZoneWithoutZ(offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_TIME));
    }

    public static String format(OffsetDateTime offsetDateTime, DateTimeFormatter formatter) {
        if (isNull(offsetDateTime)) {
            return null;
        }
        return formatTimeZoneWithoutZ(offsetDateTime.format(formatter));
    }

    public static OffsetDateTime parseOffsetDateTime(String dateTime) {
        if (isBlank(dateTime)) {
            return null;
        }

        return OffsetDateTime.parse(dateTime, DateTimeFormatter.ofPattern(OFFSET_DATE_TIME_PATTERN_DEFAULT));
    }

    public static OffsetDateTime parseOffsetDateAtStartOfDay(String date) {
        return parseOffsetDate(date, START_OF_DAY);
    }

    public static OffsetDateTime parseOffsetDateAtEndOfDay(String date) {
        return parseOffsetDate(date, END_OF_DAY);
    }

    public static String formatToOffsetDateTimeWithCustomZoneOffset(OffsetDateTime offsetDateTime, ZoneOffset zoneOffset) {
        return formatTimeZoneWithoutZ(format(offsetDateTime.withOffsetSameInstant(zoneOffset)));
    }

    public static String formatToOffsetDateWithCustomZoneOffset(OffsetDateTime offsetDateTime, ZoneOffset zoneOffset) {
        return formatToOffsetDateWithTimezone(offsetDateTime.withOffsetSameInstant(zoneOffset));
    }

    public static String formatToOffsetTimeWithCustomZoneOffset(OffsetTime offsetTime, ZoneOffset zoneOffset) {
        return formatToOffsetTimeWithTimezone(offsetTime.withOffsetSameInstant(zoneOffset));
    }

    public static String changeOffsetDateTimeZoneOffset(@NotNull String offsetDateTimeStr, ZoneOffset zoneOffset) {
        return formatToOffsetDateTimeWithCustomZoneOffset(requireNonNull(parseOffsetDateTime(offsetDateTimeStr)), zoneOffset);
    }

    public static String changeOffsetTimeZoneOffset(@NotNull String offsetTimeStr, ZoneOffset zoneOffset) {
        return formatToOffsetTimeWithCustomZoneOffset(parseOffsetTime(offsetTimeStr), zoneOffset);
    }

    public static String changeOffsetDateZoneOffset(@NotNull String offsetDateStr, ZoneOffset zoneOffset) {
        return formatToOffsetDateWithCustomZoneOffset(requireNonNull(parseOffsetDate(offsetDateStr, 12)), zoneOffset);
    }

    public static OffsetTime parseOffsetTime(String offsetTimeStr) {
        return OffsetTime.parse(offsetTimeStr);
    }

    public static String formatToOffsetTimeWithTimezone(OffsetTime offsetTime) {
        if (isNull(offsetTime)) {
            return null;
        }
        return formatTimeZoneWithoutZ(offsetTime.format(DateTimeFormatter.ISO_OFFSET_TIME));
    }

    private static OffsetDateTime parseOffsetDate(String date, @NotNull long hourOfDay) {
        if (isBlank(date)) {
            return null;
        }

        DateTimeFormatter offsetDateFormatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_OFFSET_DATE)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, hourOfDay)
                .toFormatter();

        return OffsetDateTime.parse(date, offsetDateFormatter);
    }

    public static ZoneOffset getZoneOffset(@NotNull String onDay) {
        if (!isOffsetCorrect(onDay)) {
            return null;
        }
        return ZoneOffset.of(onDay.substring(onDay.length()-6));
    }

    public static OffsetDateTime parseOffsetDateTime(@NotNull String offsetDateStr, @NotNull String offsetTimeStr) {
        OffsetTime offsetTime = parseOffsetTime(offsetTimeStr);
        OffsetDateTime offsetDateTimeAtStartOfDay = parseOffsetDateAtStartOfDay(offsetDateStr);

        return offsetDateTimeAtStartOfDay.withHour(offsetTime.getHour())
                .withMinute(offsetTime.getMinute())
                .withSecond(offsetTime.getSecond());
    }

    /**
     * Считает период между двумя датами в днях c исключением выходных
     *
     * @param start - Начальная дата
     * @param end - Конечная дата
     * @return - Длительность в днях
     */
    public static int workDaysPeriodIncludingFirstDay(LocalDate start, LocalDate end) {
        final int FIRST_DAY = 1;
        long daysBetween = Period.between(start, end).getDays() + FIRST_DAY;

        int businessDaysBetween = Math.toIntExact(Stream.iterate(start, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isWeekend.negate())
                .count());
        return businessDaysBetween;
    }

    /**
     * Рассчитать период в часах и минутах между датами одного дня
     * @param start
     * @param end
     * @return Строку в формате "1ч 30м"
     */
    public static String calculateDurationAndFormat(@NotNull OffsetDateTime start, @NotNull OffsetDateTime end) {
        String durationTemplate = "%dч %dм";
        Duration d = calculateDuration(start, end);
        return String.format(durationTemplate, d.toHours() % 25, d.toMinutes() % 60);
    }

    /**
     * Расчитать длительность курса по сумме длительностей всех мероприятий в часах
     * @param start
     * @param end
     * @return Длительность курса
     */
    public static Duration calculateDuration(OffsetDateTime start, OffsetDateTime end) {
        requireNonNull(start);
        requireNonNull(end);
        Duration d = Duration.between(start, end);
        if(d.isNegative()) {
            d = d.negated();
        }
        if (d.toHours() > 8) {
            d = Duration.ofHours(8);
        }
        return d;
    }

    private static boolean isOffsetCorrect(String value) {
        if (isBlank(value) || (!value.contains("+") && !value.contains("-"))) {
            return false;
        } else {
            return true;
        }
    }

    public static String formatTimeZoneWithoutZ(String dateTime){
        if (isNotBlank(dateTime) && endsWith(dateTime, "Z")){
            return dateTime.replace("Z", "+00:00");
        }
        return dateTime;
    }

    /**
     * Возвращает рабочий день, если {@param date} суббота или воскресение, то возвращает понедельник
     * В остальных случаях возвращает {@param date} без изменении
     */
    public static OffsetDateTime getFirstWorkingDay(@NotNull OffsetDateTime date) {
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
}
