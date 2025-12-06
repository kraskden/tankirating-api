package me.fizzika.tankirating.dto;

import lombok.Data;
import me.fizzika.tankirating.enums.DiffPeriodUnit;

import java.time.LocalDateTime;

@Data
public abstract class PeriodTrackDTO {

    protected DiffPeriodUnit period;

    protected LocalDateTime periodStart;

    protected LocalDateTime periodEnd;

    protected LocalDateTime trackStart;

    protected LocalDateTime trackEnd;

}