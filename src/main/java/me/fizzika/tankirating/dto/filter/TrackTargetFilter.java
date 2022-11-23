package me.fizzika.tankirating.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;

import java.util.Collection;
import java.util.EnumSet;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrackTargetFilter extends QueryFilter {

    TrackTargetType targetType;
    Collection<TrackTargetStatus> statuses = EnumSet.allOf(TrackTargetStatus.class);

}
