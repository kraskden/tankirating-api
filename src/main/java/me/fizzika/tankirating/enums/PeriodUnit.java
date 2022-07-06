package me.fizzika.tankirating.enums;

import lombok.AllArgsConstructor;
import me.fizzika.tankirating.model.date.DatePeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum PeriodUnit {

    DAY(stamp -> new DatePeriod(stamp, getPeriodEndDate(stamp, ChronoUnit.DAYS, 1), ChronoUnit.DAYS)),
    WEEK(stamp -> getDiffPeriod(stamp, ChronoField.DAY_OF_WEEK, ChronoUnit.WEEKS)),
    MONTH(stamp -> getDiffPeriod(stamp, ChronoField.DAY_OF_MONTH, ChronoUnit.MONTHS)),
    YEAR(stamp -> getDiffPeriod(stamp, ChronoField.DAY_OF_YEAR, ChronoUnit.YEARS)),

    // LocalDateTime.MIN...LocalDateTime.MAX is not fit into postgres timestamp range
    ALL_TIME(stamp -> new DatePeriod(
            LocalDate.EPOCH.atStartOfDay(),
            LocalDate.of(3000, 1, 1).atStartOfDay(),
            ChronoUnit.FOREVER
    ));

    public static final List<PeriodUnit> GROUP_PERIODS = Arrays.stream(PeriodUnit.values())
            .filter(p -> p != DAY)
            .collect(Collectors.toList());

    private final Function<LocalDateTime, DatePeriod> periodGenerator;

    public DatePeriod getDatePeriod(LocalDateTime stamp) {
        return periodGenerator.apply(stamp.truncatedTo(ChronoUnit.DAYS));
    }

    private static DatePeriod getDiffPeriod(LocalDateTime now, ChronoField resetField, ChronoUnit addUnit) {
        LocalDateTime startDate = getPeriodStartDate(now, resetField);
        return new DatePeriod(startDate, getPeriodEndDate(startDate, addUnit, 1), addUnit);
    }

    private static LocalDateTime getPeriodEndDate(LocalDateTime startDate, ChronoUnit addUnit, long duration) {
        return startDate.plus(duration, addUnit);
    }

    private static LocalDateTime getPeriodStartDate(LocalDateTime now, ChronoField resetField) {
        return now.with(resetField, 1);
    }

}
