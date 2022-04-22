package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.alternativa.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackEntity;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaEntityPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaModePlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaSupplyUsageTrack;
import me.fizzika.tankirating.enums.TankiSupply;
import me.fizzika.tankirating.enums.TrackActivityType;
import me.fizzika.tankirating.model.tracking.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: premium

@Mapper(componentModel = "spring")
public abstract class AlternativaTrackingMapper {

    public FullTrackModel toFullTrackModel(AlternativaTrackDTO trackDTO) {
        FullTrackModel res = new FullTrackModel();
        res.setBaseTrack(toBaseTrackModel(trackDTO));
        res.setSuppliesUsage(toSupplyUsageTrackMap(trackDTO.getSuppliesUsage()));
        res.setActivities(toActivityTrackMap(trackDTO));
        return res;
    }

    @Mapping(source = "earnedCrystals", target = "cry")
    @Mapping(source = "caughtGolds", target = "gold")
    @Mapping(target = "time", source = ".", qualifiedByName = "getFullTime")
    protected abstract BaseTrackModel toBaseTrackModel(AlternativaTrackDTO trackDTO);

    @Named("getFullTime")
    protected long getFullTime(AlternativaTrackDTO track) {
        return track.getHullsPlayed().stream()
                .map(AlternativaPlayTrack::getTimePlayed)
                .reduce(0L, Long::sum) / 1000;
    }

    private Map<String, UsageTrackModel> toSupplyUsageTrackMap(List<AlternativaSupplyUsageTrack> tracks) {
        return tracks.stream()
                .collect(Collectors.toMap(this::getSupplyName, t -> new UsageTrackModel(t.getUsages())));
    }

    private Map<TrackActivityType, ActivityTrackModel> toActivityTrackMap(AlternativaTrackDTO trackDTO) {
        Map<TrackActivityType, ActivityTrackModel> res = new EnumMap<>(TrackActivityType.class);
        res.put(TrackActivityType.HULL, toEntityActivityTrackModel(trackDTO.getHullsPlayed()));
        res.put(TrackActivityType.TURRET, toEntityActivityTrackModel(trackDTO.getTurretsPlayed()));
        res.put(TrackActivityType.MODULE, toEntityActivityTrackModel(trackDTO.getResistanceModules()));
        res.put(TrackActivityType.MODE, toModeActivityTrackModel(trackDTO.getModesPlayed()));
        return res;
    }

    private ActivityTrackModel toModeActivityTrackModel(List<AlternativaModePlayTrack> modeTracks) {
        return new ActivityTrackModel(modeTracks.stream()
                .collect(Collectors.toMap(AlternativaTrackEntity::getName, this::toPlayTrackModel)));
    }

    private ActivityTrackModel toEntityActivityTrackModel(List<AlternativaEntityPlayTrack> entityTracks) {
        Map<String, PlayTrackModel> playTrackMap = new HashMap<>();
        for (AlternativaEntityPlayTrack entityPlayTrack : entityTracks) {
            String name = entityPlayTrack.getName();
            if (playTrackMap.containsKey(name)) {
                playTrackMap.get(name).add(toPlayTrackModel(entityPlayTrack));
            } else {
                playTrackMap.put(name, toPlayTrackModel(entityPlayTrack));
            }
        }
        return new ActivityTrackModel(playTrackMap);
    }

    private PlayTrackModel toPlayTrackModel(AlternativaPlayTrack playTrack) {
        return new PlayTrackModel(playTrack.getScoreEarned(), playTrack.getTimePlayed() / 1000);
    }

    private String getSupplyName(AlternativaSupplyUsageTrack supplyUsageTrack) {
        return TankiSupply.getById(supplyUsageTrack.getId())
                .map(Enum::name)
                .orElse(String.valueOf(supplyUsageTrack.getId()));
    }

}
