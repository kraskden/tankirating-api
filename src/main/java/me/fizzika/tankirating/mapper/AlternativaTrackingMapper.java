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

    public TrackFullModel toFullTrackModel(AlternativaTrackDTO trackDTO) {
        TrackFullModel res = new TrackFullModel();
        res.setBase(toBaseTrackModel(trackDTO));
        res.setSupplies(toSupplyUsageTrackMap(trackDTO.getSuppliesUsage()));
        res.setActivities(toActivityTrackMap(trackDTO));
        return res;
    }

    @Mapping(source = "earnedCrystals", target = "cry")
    @Mapping(source = "caughtGolds", target = "gold")
    @Mapping(target = "time", source = ".", qualifiedByName = "getFullTime")
    protected abstract TrackBaseModel toBaseTrackModel(AlternativaTrackDTO trackDTO);

    @Named("getFullTime")
    protected long getFullTime(AlternativaTrackDTO track) {
        return track.getHullsPlayed().stream()
                .map(AlternativaPlayTrack::getTimePlayed)
                .reduce(0L, Long::sum) / 1000;
    }

    private Map<String, TrackUsageModel> toSupplyUsageTrackMap(List<AlternativaSupplyUsageTrack> tracks) {
        return tracks.stream()
                .collect(Collectors.toMap(this::getSupplyName, t -> new TrackUsageModel(t.getUsages())));
    }

    private Map<TrackActivityType, TrackActivityModel> toActivityTrackMap(AlternativaTrackDTO trackDTO) {
        Map<TrackActivityType, TrackActivityModel> res = new EnumMap<>(TrackActivityType.class);
        res.put(TrackActivityType.HULL, toEntityActivityTrackModel(trackDTO.getHullsPlayed()));
        res.put(TrackActivityType.TURRET, toEntityActivityTrackModel(trackDTO.getTurretsPlayed()));
        res.put(TrackActivityType.MODULE, toEntityActivityTrackModel(trackDTO.getResistanceModules()));
        res.put(TrackActivityType.MODE, toModeActivityTrackModel(trackDTO.getModesPlayed()));
        return res;
    }

    private TrackActivityModel toModeActivityTrackModel(List<AlternativaModePlayTrack> modeTracks) {
        return new TrackActivityModel(modeTracks.stream()
                .collect(Collectors.toMap(AlternativaTrackEntity::getName, this::toPlayTrackModel)));
    }

    private TrackActivityModel toEntityActivityTrackModel(List<AlternativaEntityPlayTrack> entityTracks) {
        Map<String, TrackPlayModel> playTrackMap = new HashMap<>();
        for (AlternativaEntityPlayTrack entityPlayTrack : entityTracks) {
            String name = entityPlayTrack.getName();
            if (playTrackMap.containsKey(name)) {
                playTrackMap.get(name).add(toPlayTrackModel(entityPlayTrack));
            } else {
                playTrackMap.put(name, toPlayTrackModel(entityPlayTrack));
            }
        }
        return new TrackActivityModel(playTrackMap);
    }

    private TrackPlayModel toPlayTrackModel(AlternativaPlayTrack playTrack) {
        return new TrackPlayModel(playTrack.getScoreEarned(), playTrack.getTimePlayed() / 1000);
    }

    private String getSupplyName(AlternativaSupplyUsageTrack supplyUsageTrack) {
        return TankiSupply.getById(supplyUsageTrack.getId())
                .map(Enum::name)
                .orElse(String.valueOf(supplyUsageTrack.getId()));
    }

}
