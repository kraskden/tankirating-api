package me.fizzika.tankirating.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.DiffPeriodUnit;

import java.time.LocalDateTime;
import org.springframework.data.web.PagedModel;

@Data
@AllArgsConstructor
public class RatingDTO {

    private DiffPeriodUnit period;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private PagedModel<AccountRatingDTO> ratingData;

}