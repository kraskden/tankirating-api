package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represent time period: [start, end)
 */
@Data
@AllArgsConstructor
public class DatePeriod {

    private LocalDateTime start;

    private LocalDateTime end;

    private ChronoUnit unit;

    public DatePeriod sub(int offset) {
        if (unit == ChronoUnit.FOREVER) {
            return new DatePeriod(start, end, unit);
        } else {
            return new DatePeriod(start.minus(offset, unit), end.minus(offset, unit), unit);
        }
    }

}
