package me.fizzika.tankirating.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackHeatMapDTO {

    private LocalDateTime timestamp;

    private Long time;

    private Integer golds;

    private Integer premiumDays;

}
