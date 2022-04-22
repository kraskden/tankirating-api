package me.fizzika.tankirating.dto.alternativa.track.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaUsageTrack;

@Getter
@Setter
@ToString(callSuper = true)
public class AlternativaSupplyUsageTrack extends AlternativaUsageTrack {

    private long id;
    private String name;

}
