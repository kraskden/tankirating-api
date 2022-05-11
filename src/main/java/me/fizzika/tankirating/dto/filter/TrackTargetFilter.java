package me.fizzika.tankirating.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.enums.track.TrackTargetType;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrackTargetFilter extends QueryFilter {

    TrackTargetType targetType;

}
