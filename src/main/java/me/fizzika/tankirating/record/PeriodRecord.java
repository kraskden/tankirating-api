package me.fizzika.tankirating.record;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.enums.DiffPeriodUnit;
import me.fizzika.tankirating.model.date.DatePeriod;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class PeriodRecord<I> extends IdRecord<I> {

    @Enumerated(EnumType.STRING)
    protected DiffPeriodUnit period;

    protected LocalDateTime periodStart;

    protected LocalDateTime periodEnd;

    protected LocalDateTime trackStart;

    protected LocalDateTime trackEnd;

    public void setPeriodDates(DatePeriod datePeriod) {
        setPeriodStart(datePeriod.getStart());
        setPeriodEnd(datePeriod.getEnd());
    }

}