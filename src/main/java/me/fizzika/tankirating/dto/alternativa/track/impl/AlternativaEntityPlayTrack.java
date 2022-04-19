package me.fizzika.tankirating.dto.alternativa.track.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaPlayTrack;

@Getter
@Setter
@ToString(callSuper = true)
public class AlternativaEntityPlayTrack extends AlternativaPlayTrack {

    private Long id;

    private Integer grade;

    private String name;

    @Override
    public String getId() {
        return id.toString();
    }

}
