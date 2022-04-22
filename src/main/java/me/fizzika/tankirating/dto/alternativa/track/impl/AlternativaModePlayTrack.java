package me.fizzika.tankirating.dto.alternativa.track.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaPlayTrack;

@Getter
@Setter
@ToString(callSuper = true)
public class AlternativaModePlayTrack extends AlternativaPlayTrack {

    private String name;
    private String type;

    @Override
    public String getName() {
        return type;
    }

}
