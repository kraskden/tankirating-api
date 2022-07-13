package me.fizzika.tankirating.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.PeriodUnit;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RatingDTO {

    private PeriodUnit period;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private Page<AccountRatingDTO> ratingData;

}
