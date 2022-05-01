package me.fizzika.tankirating.dto.alternativa.track;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class AlternativaPlayTrack extends AlternativaTrackEntity {

    private int scoreEarned;

    private long timePlayed;

}
