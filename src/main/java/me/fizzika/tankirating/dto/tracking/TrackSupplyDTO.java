package me.fizzika.tankirating.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackSupplyDTO {

    private String name;

    private long usages;

}