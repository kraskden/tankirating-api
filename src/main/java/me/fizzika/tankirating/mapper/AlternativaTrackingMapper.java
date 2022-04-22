package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.TargetDTO;
import me.fizzika.tankirating.dto.alternativa.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaEntityPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaModePlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaSupplyUsageTrack;
import me.fizzika.tankirating.dto.tracking.FullTrackingDTO;
import me.fizzika.tankirating.dto.tracking.track_data.ActivityTrack;
import me.fizzika.tankirating.dto.tracking.track_data.BaseTrackData;
import me.fizzika.tankirating.dto.tracking.track_data.FullTrackData;
import me.fizzika.tankirating.dto.tracking.track_data.SupplyTrack;
import me.fizzika.tankirating.enums.TankiSupply;
import me.fizzika.tankirating.enums.TrackActivityType;
import me.fizzika.tankirating.enums.TrackTargetType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AlternativaTrackingMapper {

    public FullTrackingDTO toTrackingDTO(AlternativaTrackDTO track, LocalDateTime timestamp) {
        FullTrackingDTO res = new FullTrackingDTO();

        res.setTarget(new TargetDTO(track.getName(), TrackTargetType.ACCOUNT));
        res.setTimestamp(timestamp);
        res.setTrack(toFullTrackData(track));

        return res;
    }

    private FullTrackData toFullTrackData(AlternativaTrackDTO track) {
        FullTrackData res = new FullTrackData();
        setBaseTrackData(res, track);
        res.setActivities(getActivities(track));
        res.setSupplies(getSupplies(track));
        return res;
    }

    private void setBaseTrackData(BaseTrackData res, AlternativaTrackDTO track) {
        res.setCry(track.getEarnedCrystals());
        res.setKills(track.getKills());
        res.setDeaths(track.getDeaths());
        res.setScore(track.getScore());
        res.setTime(getFullTime(track));
        res.setGold(track.getCaughtGolds());
    }

    private Long getFullTime(AlternativaTrackDTO track) {
        return track.getHullsPlayed().stream()
                .map(AlternativaPlayTrack::getTimePlayed)
                .reduce(0L, Long::sum) / 1000;
    }

    private Map<TrackActivityType, List<ActivityTrack>> getActivities(AlternativaTrackDTO track) {
        Map<TrackActivityType, List<ActivityTrack>> res = new EnumMap<>(TrackActivityType.class);
        res.put(TrackActivityType.HULL, getEntityActivityTrack(track.getHullsPlayed()));
        res.put(TrackActivityType.TURRET, getEntityActivityTrack(track.getTurretsPlayed()));
        res.put(TrackActivityType.MODULE, getEntityActivityTrack(track.getResistanceModules()));
        res.put(TrackActivityType.MODE, getModesActivityTrack(track.getModesPlayed()));
        return res;
    }

    private List<ActivityTrack> getModesActivityTrack(List<AlternativaModePlayTrack> modePlayTracks) {
        return modePlayTracks.stream()
                .map(this::toActivityTrack)
                .collect(Collectors.toList());
    }

    private List<ActivityTrack> getEntityActivityTrack(List<AlternativaEntityPlayTrack> entityTracks) {
        Map<String, ActivityTrack> resultMap = new HashMap<>();
        for (AlternativaEntityPlayTrack alternativaTrack : entityTracks) {
            if (resultMap.containsKey(alternativaTrack.getName())) {
                ActivityTrack track = resultMap.get(alternativaTrack.getName());
                track.setTime(track.getTime() + alternativaTrack.getTimePlayed() / 1000);
                track.setScore(track.getScore() + alternativaTrack.getScoreEarned());
            } else {
                resultMap.put(alternativaTrack.getName(), toActivityTrack(alternativaTrack));
            }
        }
        return new ArrayList<>(resultMap.values());
    }

    private List<SupplyTrack> getSupplies(AlternativaTrackDTO track) {
        return track.getSuppliesUsage().stream()
                .map(this::toSupplyTrack)
                .collect(Collectors.toList());
    }

    private ActivityTrack toActivityTrack(AlternativaPlayTrack playTrack) {
        return new ActivityTrack(playTrack.getName(), playTrack.getScoreEarned(), playTrack.getTimePlayed());
    }

    private SupplyTrack toSupplyTrack(AlternativaSupplyUsageTrack usageTrack) {
        String supplyName = TankiSupply.getById(usageTrack.getId())
                .map(Enum::name)
                .orElse(String.valueOf(usageTrack.getId()));

        return new SupplyTrack(supplyName, usageTrack.getUsages());
    }

}
