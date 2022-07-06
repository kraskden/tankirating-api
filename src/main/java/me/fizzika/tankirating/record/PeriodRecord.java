package me.fizzika.tankirating.record;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.model.date.DatePeriod;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class PeriodRecord<I> extends IdRecord<I> {

    @Enumerated(EnumType.STRING)
    protected PeriodUnit period;

    protected LocalDateTime periodStart;

    protected LocalDateTime periodEnd;

    protected LocalDateTime trackStart;

    protected LocalDateTime trackEnd;

    public void setPeriodDates(DatePeriod datePeriod) {
        setPeriodStart(datePeriod.getStart());
        setPeriodEnd(datePeriod.getEnd());
    }

}
