package me.fizzika.tankirating.dto.alternativa.track;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AlternativaUsageTrack extends AlternativaTrackEntity {

    private long usages;

}
