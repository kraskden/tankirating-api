package me.fizzika.tankirating.dto.tracking.track_data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplyTrack {

    private String name;

    private long usages;

}
