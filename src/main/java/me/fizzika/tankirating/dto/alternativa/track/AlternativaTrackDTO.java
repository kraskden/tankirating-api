package me.fizzika.tankirating.dto.alternativa.track;

import lombok.Data;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaEntityPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaModePlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaSupplyUsageTrack;

import java.util.List;

@Data
public class AlternativaTrackDTO {

    private String name;

    private int caughtGolds;
    private int deaths;
    private int earnedCrystals;
    private int gearScore;
    private boolean hasPremium;
    private int kills;
    private int rank;
    private int score;

    private List<AlternativaEntityPlayTrack> dronesPlayed;
    private List<AlternativaEntityPlayTrack> hullsPlayed;
    private List<AlternativaEntityPlayTrack> turretsPlayed;
    private List<AlternativaEntityPlayTrack> resistanceModules;

    private List<AlternativaModePlayTrack> modesPlayed;


    private List<AlternativaSupplyUsageTrack> suppliesUsage;

}
