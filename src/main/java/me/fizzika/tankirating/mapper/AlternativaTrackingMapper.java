package me.fizzika.tankirating.mapper;

import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackEntity;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaEntityPlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaModePlayTrack;
import me.fizzika.tankirating.dto.alternativa.track.impl.AlternativaSupplyUsageTrack;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.enums.track.TankiSupply;
import me.fizzika.tankirating.model.AccountData;
import me.fizzika.tankirating.model.track_data.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class AlternativaTrackingMapper {

    public AccountData<TrackFullData> toFullAccountData(AlternativaTrackDTO trackDTO) {
        TrackFullData res = new TrackFullData();
        res.setBase(toBaseTrackModel(trackDTO));
        res.setSupplies(toSupplyUsageTrackMap(trackDTO.getSuppliesUsage()));
        res.setActivities(toActivityTrackMap(trackDTO));
        return new AccountData<>(trackDTO.isHasPremium(), res);
    }

    @Mapping(source = "earnedCrystals", target = "cry")
    @Mapping(source = "caughtGolds", target = "gold")
    @Mapping(target = "time", source = ".", qualifiedByName = "getFullTime")
    protected abstract TrackBaseData toBaseTrackModel(AlternativaTrackDTO trackDTO);

    @Named("getFullTime")
    protected long getFullTime(AlternativaTrackDTO track) {
        long hullsTime = getTimePlayed(track.getHullsPlayed());
        long turretsTime = getTimePlayed(track.getTurretsPlayed());
        long modesTime = getTimePlayed(track.getModesPlayed());
        return Math.max(modesTime, Math.max(hullsTime, turretsTime));
    }

    protected long getTimePlayed(Collection<? extends AlternativaPlayTrack> playTracks) {
        return playTracks.stream()
                .mapToLong(AlternativaPlayTrack::getTimePlayed)
                .sum() / 1000;
    }

    private Map<String, TrackUsageData> toSupplyUsageTrackMap(List<AlternativaSupplyUsageTrack> tracks) {
        return tracks.stream()
                .collect(Collectors.toMap(this::getSupplyName, t -> new TrackUsageData(t.getUsages())));
    }

    private Map<TankiEntityType, TrackActivityData> toActivityTrackMap(AlternativaTrackDTO trackDTO) {
        Map<TankiEntityType, TrackActivityData> res = new EnumMap<>(TankiEntityType.class);
        res.put(TankiEntityType.HULL, toEntityActivityTrackModel(trackDTO.getHullsPlayed()));
        res.put(TankiEntityType.TURRET, toEntityActivityTrackModel(trackDTO.getTurretsPlayed()));
        res.put(TankiEntityType.MODULE, toEntityActivityTrackModel(trackDTO.getResistanceModules()));
        res.put(TankiEntityType.MODE, toModeActivityTrackModel(trackDTO.getModesPlayed()));
        return res;
    }

    private TrackActivityData toModeActivityTrackModel(List<AlternativaModePlayTrack> modeTracks) {
        return new TrackActivityData(modeTracks.stream()
                .collect(Collectors.toMap(AlternativaTrackEntity::getName, this::toPlayTrackModel)));
    }

    private TrackActivityData toEntityActivityTrackModel(List<AlternativaEntityPlayTrack> entityTracks) {
        Map<String, TrackPlayData> playTrackMap = new HashMap<>();
        for (AlternativaEntityPlayTrack entityPlayTrack : entityTracks) {
            String name = entityPlayTrack.getName();
            if (playTrackMap.containsKey(name)) {
                playTrackMap.get(name).add(toPlayTrackModel(entityPlayTrack));
            } else {
                playTrackMap.put(name, toPlayTrackModel(entityPlayTrack));
            }
        }
        return new TrackActivityData(playTrackMap);
    }

    private TrackPlayData toPlayTrackModel(AlternativaPlayTrack playTrack) {
        return new TrackPlayData(playTrack.getScoreEarned(), playTrack.getTimePlayed() / 1000);
    }

    private String getSupplyName(AlternativaSupplyUsageTrack supplyUsageTrack) {
        return TankiSupply.getById(supplyUsageTrack.getId())
                .map(Enum::name)
                .orElse(String.valueOf(supplyUsageTrack.getId()));
    }

}