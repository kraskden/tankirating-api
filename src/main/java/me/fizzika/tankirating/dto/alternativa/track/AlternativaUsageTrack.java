package me.fizzika.tankirating.dto.alternativa.track;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AlternativaUsageTrack extends AlternativaTrackEntity {

    private Long usages;

}
