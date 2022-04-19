package me.fizzika.tankirating.dto.alternativa.track;

import lombok.*;

@Getter
@Setter
@ToString
public abstract class AlternativaPlayTrack extends AlternativaTrackEntity {

    private Integer scoreEarned;

    private Long timePlayed;

}
