package me.fizzika.tankirating.dto.online;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.dto.PeriodTrackDTO;


@Data
@EqualsAndHashCode(callSuper = true)
public class OnlinePcuDTO extends PeriodTrackDTO {

    private Integer onlinePcu;

    private Integer inbattlesPcu;

}
