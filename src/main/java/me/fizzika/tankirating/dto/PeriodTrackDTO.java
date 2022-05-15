package me.fizzika.tankirating.dto;

import lombok.Data;
import me.fizzika.tankirating.enums.PeriodUnit;

import java.time.LocalDateTime;

@Data
public abstract class PeriodTrackDTO {

    protected PeriodUnit period;

    protected LocalDateTime periodStart;

    protected LocalDateTime periodEnd;

    protected LocalDateTime trackStart;

    protected LocalDateTime trackEnd;

}
