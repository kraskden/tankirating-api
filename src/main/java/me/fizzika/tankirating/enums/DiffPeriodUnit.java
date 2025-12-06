package me.fizzika.tankirating.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.model.date.DatePeriod;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(type = "String", allowableValues = {
        "day", "week", "month", "year", "all_time"
})
public enum DiffPeriodUnit {

    DAY(ChronoUnit.DAYS, DiffPeriodUnit::getDayDiffPeriod),
    WEEK(ChronoUnit.WEEKS, ChronoField.DAY_OF_WEEK),
    MONTH(ChronoUnit.MONTHS, ChronoField.DAY_OF_MONTH),
    YEAR(ChronoUnit.YEARS, ChronoField.DAY_OF_YEAR),

    // LocalDateTime.MIN...LocalDateTime.MAX is not fit into postgres timestamp range
    ALL_TIME(ChronoUnit.FOREVER, stamp -> new DatePeriod(
            LocalDate.EPOCH.atStartOfDay(),
            LocalDate.of(3000, 1, 1).atStartOfDay(),
            ChronoUnit.FOREVER
    ));

    DiffPeriodUnit(ChronoUnit unit, Function<LocalDateTime, DatePeriod> periodGenerator) {
        this.chronoUnit = unit;
        this.periodGenerator = periodGenerator;
    }

    DiffPeriodUnit(ChronoUnit unit, ChronoField resetField) {
        this.chronoUnit = unit;
        this.periodGenerator = stamp -> getDiffPeriod(stamp, resetField, chronoUnit);
    }

    public static final List<DiffPeriodUnit> GROUP_PERIODS = Arrays.stream(DiffPeriodUnit.values())
                                                                   .filter(p -> p != DAY)
                                                                   .collect(Collectors.toList());

    private final ChronoUnit chronoUnit;
    private final Function<LocalDateTime, DatePeriod> periodGenerator;

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

    public DatePeriod getDatePeriod(LocalDateTime stamp) {
        return periodGenerator.apply(stamp.truncatedTo(ChronoUnit.DAYS));
    }

    public String getDBTruncatePeriod() {
        if (this == ALL_TIME) {
            throw new ExternalException("Invalid truncate period", HttpStatus.BAD_REQUEST);
        }
        return name().toLowerCase();
    }

    private static DatePeriod getDiffPeriod(LocalDateTime now, ChronoField resetField, ChronoUnit addUnit) {
        LocalDateTime startDate = getPeriodStartDate(now, resetField);
        return new DatePeriod(startDate, getPeriodEndDate(startDate, addUnit, 1), addUnit);
    }

    private static DatePeriod getDayDiffPeriod(LocalDateTime now) {
        return new DatePeriod(now, getPeriodEndDate(now, ChronoUnit.DAYS, 1),
            ChronoUnit.DAYS);
    }

    private static LocalDateTime getPeriodEndDate(LocalDateTime startDate, ChronoUnit addUnit, long duration) {
        return startDate.plus(duration, addUnit);
    }

    private static LocalDateTime getPeriodStartDate(LocalDateTime now, ChronoField resetField) {
        return now.with(resetField, 1);
    }

}