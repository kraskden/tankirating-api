package me.fizzika.tankirating.dto.alternativa.track.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaPlayTrack;

@Getter
@Setter
@ToString(callSuper = true)
public class AlternativaEntityPlayTrack extends AlternativaPlayTrack {

    private long id;

    private int grade;

    private String name;

}
