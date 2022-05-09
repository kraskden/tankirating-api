package me.fizzika.tankirating.dto.filter;

import lombok.Data;
import me.fizzika.tankirating.enums.TrackFormat;

@Data
public class TrackFormatFilter {

    TrackFormat format = TrackFormat.FULL;

}
