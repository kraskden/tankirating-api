package me.fizzika.tankirating.dto.alternativa;

import lombok.Data;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaEntityPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaModePlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaSupplyUsageTrack;

import java.util.List;

@Data
public class AlternativaTrackDTO {

    private String name;

    private Integer caughtGolds;
    private Integer deaths;
    private Integer earnedCrystals;
    private Integer gearScore;
    private Boolean hasPremium;
    private Integer kills;
    private Integer rank;
    private Integer score;

    private List<AlternativaEntityPlayTrack> dronesPlayed;
    private List<AlternativaEntityPlayTrack> hullsPlayed;
    private List<AlternativaEntityPlayTrack> turretsPlayed;
    private List<AlternativaModePlayTrack> modesPlayed;

    private List<AlternativaSupplyUsageTrack> suppliesUsage;

}
