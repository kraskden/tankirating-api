package me.fizzika.tankirating.enums;

import lombok.AllArgsConstructor;
import me.fizzika.tankirating.model.DatePeriod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@AllArgsConstructor
public enum TrackDiffPeriod {

    DAY(stamp -> new DatePeriod(stamp, getPeriodEndDate(stamp, ChronoUnit.DAYS, 1))),
    WEEK(stamp -> getDiffPeriod(stamp, ChronoField.DAY_OF_WEEK, ChronoUnit.WEEKS)),
    MONTH(stamp -> getDiffPeriod(stamp, ChronoField.DAY_OF_MONTH, ChronoUnit.MONTHS)),
    YEAR(stamp -> getDiffPeriod(stamp, ChronoField.DAY_OF_YEAR, ChronoUnit.YEARS)),
    ALL_TIME(stamp -> new DatePeriod(LocalDateTime.MIN, LocalDateTime.MAX));

    private final Function<LocalDateTime, DatePeriod> periodGenerator;

    public DatePeriod getDatePeriod(LocalDateTime stamp) {
        return periodGenerator.apply(stamp.truncatedTo(ChronoUnit.DAYS));
    }

    private static DatePeriod getDiffPeriod(LocalDateTime now, ChronoField resetField, ChronoUnit addUnit) {
        LocalDateTime startDate = getPeriodStartDate(now, resetField);
        return new DatePeriod(startDate, getPeriodEndDate(startDate, addUnit, 1));
    }

    private static LocalDateTime getPeriodEndDate(LocalDateTime startDate, ChronoUnit addUnit, long duration) {
        return startDate.plus(duration, addUnit);
    }

    private static LocalDateTime getPeriodStartDate(LocalDateTime now, ChronoField resetField) {
        return now.with(resetField, 0);
    }

}
