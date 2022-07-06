package me.fizzika.tankirating.model.date;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PeriodDiffDates {

    protected LocalDateTime periodStart;

    protected LocalDateTime periodEnd;

    protected LocalDateTime trackStart;

    protected LocalDateTime trackEnd;

    public PeriodDiffDates(DateRange period, DateRange track) {
        this.periodStart = period.getStart();
        this.periodEnd = period.getEnd();
        this.trackStart = track.getStart();
        this.trackEnd = track.getEnd();
    }

    public DateRange toPeriodRange() {
        return new DateRange(periodStart, periodEnd);
    }

    public DateRange toTrackRange() {
        return new DateRange(trackStart, trackEnd);
    }

}
