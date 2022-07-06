package me.fizzika.tankirating.model.date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represent time period: [start, end)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatePeriod extends DateRange {

    private ChronoUnit unit;

    public DatePeriod(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        super(start, end);
        this.unit = unit;
    }

    public DatePeriod sub(int offset) {
        if (unit == ChronoUnit.FOREVER) {
            return new DatePeriod(start, end, unit);
        } else {
            return new DatePeriod(start.minus(offset, unit), end.minus(offset, unit), unit);
        }
    }

}
