package me.fizzika.tankirating.dto.tracking;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TrackActivitiesDTO {

    private List<TrackActivityDTO> hulls = new ArrayList<>();

    private List<TrackActivityDTO> turrets = new ArrayList<>();

    private List<TrackActivityDTO> modes = new ArrayList<>();

    private List<TrackActivityDTO> modules = new ArrayList<>();

}
